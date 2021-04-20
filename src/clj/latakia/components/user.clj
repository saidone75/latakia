(ns latakia.components.user
  (:require [clojure.string :as s]
            [secrets.core :as secrets]
            [latakia.components.prosody :as prosody]
            [latakia.components.mail :as mail]
            [latakia.components.db :as db]
            [latakia.components.dictionary :as dict]
            [latakia.components.crypto :as crypto]
            [taoensso.tempura :as tempura :refer [tr]]))

(def opts {:dict (dict/get-dictionary "it-IT")})
(def tp (partial tr opts [:it-IT]))

(defn email-valid? [email]
  (and (not (s/blank? email))
       (= email (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" email))))

(defn username-valid? [username]
  (and (not (s/blank? username))
       (= username (re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" username))))

(defn password-valid? [password]
  (and (not (s/blank? password))
       (= password (re-matches
                    #"^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{25,}"
                    password))))

(defn register-user [{email :email username :username password :password}]
  (db/load-db!)
  (let [result (atom {:error false :message nil})]
    (when-not (and
               (email-valid? email)
               (username-valid? username)
               (password-valid? password))
      (swap! result assoc :error true :message (tp [:error])))
    (when-not (and
               (not (contains? @db/pending-requests (keyword username)))
               (prosody/adduser (str username "@3x1t.org") (secrets/token-urlsafe 16)))
      (swap! result assoc :error true :message (tp [:username-taken])))
    (when-not (:error @result)
      (swap! db/pending-requests assoc (keyword username)
             {:email email
              :password (crypto/encrypt password)
              :token (secrets/token-urlsafe 32)
              :timestamp (quot (System/currentTimeMillis) 1000)})
      (db/write-db!)
      (mail/send-validation-mail username email (:token (get @db/pending-requests (keyword username))))
      (swap! result assoc :message (tp [:mail-sent-message])))
    @result))

(defn activate-user [{token :token}]
  (db/load-db!)
  (let [entry (first (filter #(= token (:token (val %))) @db/pending-requests))]
    (if (or (nil? entry)
            (not (prosody/passwd (str (name (key entry)) "@3x1t.org") (crypto/decrypt (:password (val entry))))))
      false
      (do
        (swap! db/pending-requests dissoc (key entry))
        (db/write-db!)
        (mail/send-activated-mail (name (key entry)) (:email (val entry)))))))
