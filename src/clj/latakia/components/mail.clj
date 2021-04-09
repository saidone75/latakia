(ns latakia.components.mail
  (:require [latakia.config :as config]
            [immuconf.config :as immu]
            [postal.core :as postal]))

(defn send-validation-mail [email token]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :validation-email :from) 
      :to email
      :subject (immu/get config :validation-email :subject)
      :body (str (immu/get config :validation-email :body)
                 (immu/get config :activate-user-scheme)
                 "://"
                 (immu/get config :activate-user-host)
                 ":"
                 (immu/get config :activate-user-port)
                 "/activate-user?token=" token)})
    true))

(defn send-activated-mail [email]
  (let [config (config/config)]
    (postal/send-message
     {:host (immu/get config :mail-server-host)}
     {:from (immu/get config :user-activated-email :from) 
      :to email
      :subject (immu/get config :user-activated-email :subject)
      :body (immu/get config :user-activated-email :body)})
    true))
