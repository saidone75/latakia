(ns latakia.components.mail
  (:require [latakia.config :as config]
            [immuconf.config :as immu]
            [postal.core :as postal]
            [latakia.components.dictionary :as dict]
            [taoensso.tempura :as tempura :refer [tr]]))

(def opts {:dict (dict/get-dictionary "it-IT")})
(def tp (partial tr opts [:it-IT]))

(defn send-validation-notify-mail [username email]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :validation-email :from) 
      :to (immu/get config :validation-email :notify-address)
      :subject (tp [:validation-notify-email-subject])
      :body (str (tp [:validation-notify-email-body] [username email]))})))

(defn send-activated-notify-mail [username email]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :validation-email :from) 
      :to (immu/get config :user-activated-email :notify-address)
      :subject (tp [:user-activated-notify-email-subject] [username])
      :body (str (tp [:user-activated-notify-email-body] [username email]))})))

(defn send-validation-mail [username email token]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :validation-email :from) 
      :to email
      :subject (tp [:validation-email-subject])
      :body (str (tp [:validation-email-body])
                 (immu/get config :activate-user-scheme)
                 "://"
                 (immu/get config :activate-user-host)
                 ":"
                 (immu/get config :activate-user-port)
                 "/activate-user?token=" token)})
    (when (immu/get config :validation-email :notify-address)
      (send-validation-notify-mail username email))
    true))

(defn send-activated-mail [username email]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :user-activated-email :from) 
      :to email
      :subject (tp [:user-activated-email-subject])
      :body (tp [:user-activated-email-body])})
    (when (immu/get config :user-activated-email :notify-address)
      (send-activated-notify-mail username email))
    true))
