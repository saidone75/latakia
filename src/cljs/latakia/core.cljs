(ns latakia.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [ajax.core :refer [GET]]
            [cognitect.transit :as t]
            [clojure.string :as s]
            [taoensso.tempura :as tempura :refer [tr]]))

(enable-console-print!)

(def dictionary (r/atom {}))

(defn dictionary-handler [response]
  (let [r (t/reader :json)
        response (t/read r response)
        opts {:dict response}
        tp (partial tr opts [:it-IT])]
    (run!
     #(swap! dictionary assoc (key %) (tp [(key %)]))
     (:it-IT response))))

(defn load-dictionary []
  (GET "/dictionary"
       {:response-format :text
        :handler dictionary-handler}))

(load-dictionary)

(defonce window-width  (.-innerWidth js/window))
(defonce window-height  (.-innerHeight js/window))
(defonce input-class (if (> (/ window-width window-height) 1)
                       "large"
                       "small"))

(def messages (r/atom {}))

(def s (r/atom {}))

(defn- hide-message [id]
  (-> (.getElementById js/document id) (aget "classList") (.remove "show-message-error"))
  (-> (.getElementById js/document id) (aget "classList") (.remove "show-message-success")))

(defn- toggle-message [id error]
  (hide-message id)
  (-> (.getElementById js/document id) (aget "classList") (.toggle (if-not error "show-message-success" "show-message-error"))))

(defn- show-errors []
  (when
      (or
       (contains? @messages :email)
       (contains? @messages :username)
       (contains? @messages :password)
       (contains? @messages :password-repeat))
    (toggle-message "message" true)))

(defn- reset-form []
  (run!
   #(swap! s dissoc %)
   [:email :username :password :password-repeat]))

(defn- response-handler [response]
  (let [r (t/reader :json)
        response (t/read r response)]
    (swap! messages assoc :response (:message response))
    (toggle-message "message" (:error response))
    (when-not (:error response)
      (reset-form))))

(defn- register-user! [s]
  (GET "/register-user"
       {:params
        {:email (:email @s)
         :username (:username @s)
         :password (:password @s)}
        :response-format :text
        :handler response-handler}))

(defn- validate-email []
  (let [email (:email @s)
        pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (if (s/blank? email)
      (swap! messages assoc :email (:mail-address-empty @dictionary))
      (if (re-matches pattern email)
        (swap! messages dissoc :email)
        (swap! messages assoc :email (:mail-address-invalid @dictionary))))))

(defn- validate-username []
  (let [username (:username @s)
        pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"]
    (if (s/blank? username)
      (swap! messages assoc :username (:username-empty @dictionary))
      (if (re-matches pattern username)
        (swap! messages dissoc :username)
        (swap! messages assoc :username (:username-invalid @dictionary)))
      )))

(defn- validate-password []
  (let [password (:password @s)]
    (if (s/blank? password)
      (swap! messages assoc :password (:password-empty @dictionary))
      (if (re-matches
           #"^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{25,}"
           password)
        (swap! messages dissoc :password)
        (swap! messages assoc :password (:password-invalid @dictionary))))))

(defn- validate-password-repeat []
  (if (or (= (:password-repeat @s) (:password @s))
          (and (s/blank? (:password-repeat @s))
               (s/blank? (:password @s))))
    (swap! messages dissoc :password-repeat)
    (swap! messages assoc :password-repeat (:password-mismatch @dictionary))))

(defn- validate-form? []
  (swap! messages dissoc :response)
  (validate-email)
  (validate-username)
  (validate-password)
  (validate-password-repeat)
  (and
   (not (contains? @messages :email))
   (not (contains? @messages :username))
   (not (contains? @messages :password))
   (not (contains? @messages :password-repeat))))

(defn- form []
  [:form {:class (let [ratio (/ window-width window-height)]
                   (if (> ratio 1)
                     "form large"
                     "form small"))
          :on-submit (fn [e]
                       (.preventDefault e)
                       (if (validate-form?)
                         (do
                           (hide-message "message")
                           (register-user! s))
                         (show-errors)))}
   [:input {:class input-class
            :type :text :placeholder (:mail-address-placeholder @dictionary)
            :value (or (:email @s) "")
            :on-change (fn [e]
                         (swap! s assoc :email (-> e .-target .-value)))}]
   [:br]
   [:input {:class input-class
            :type :text :placeholder (:username-placeholder @dictionary)
            :value (or (:username @s) "")
            :on-change (fn [e]
                         (swap! s assoc :username (-> e .-target .-value)))}]
   [:br]
   [:input {:class input-class
            :type :password :placeholder (:password-placeholder @dictionary)
            :value (or (:password @s) "")
            :on-change (fn [e]
                         (swap! s assoc :password (-> e .-target .-value)))}]
   [:br]
   [:input {:class input-class
            :type :password :placeholder (:password-repeat-placeholder @dictionary)
            :value (or (:password-repeat @s) "")
            :on-change (fn [e]
                         (swap! s assoc :password-repeat (-> e .-target .-value)))}]
   [:br]
   [:input {:class input-class :type :submit :value (or (:submit @dictionary) "")}]])

(defn- page []
  [:div {:class "page"}
   [:div {:class "form"}
    [:img {:src "https://www.3x1t.org/wp-content/uploads/2020/01/logo-150x89.png"}]
    [:div {:class "intro"} (:intro-text @dictionary)]
    (form)
    [:div {:id "message" :class "message"}
     (map #(list (val %) ^{:key (random-uuid)} [:br] ^{:key (random-uuid)} [:hr]) (butlast @messages))
     (when (last @messages) (val (last @messages)))
     ]
    ]
   [:div {:class "footer"}
    (:foot-note @dictionary)]
   ])

(defn render []
  (rdom/render [page] (.getElementById js/document "app")))
