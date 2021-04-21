(ns latakia.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response redirect]]
            [ring.middleware.reload :refer [wrap-reload]]
            [latakia.components.user :as user]
            [latakia.components.dictionary :as dict]
            [latakia.config :as config]
            [immuconf.config :as immu]
            [cognitect.transit :as t]
            [taoensso.tempura :as tempura :refer [tr]]
            [project-clj.core :as project-clj]))

(import [java.io ByteArrayOutputStream])

(def opts {:dict (dict/get-dictionary "it-IT")})
(def tp (partial tr opts [:it-IT]))

(def my-routes
  (routes
   (resources "/")
   (GET "/register-user" request
        (let [out (ByteArrayOutputStream. 4096)
              writer (t/writer out :json)
              result (user/register-user (:params request))]
          (t/write writer {:error (:error result) :message (:message result)})
          {:body (.toString out)}))
   (GET "/activate-user" request
        (if (user/activate-user (:params request))
          (-> "public/success.html"
              io/resource
              io/input-stream
              response
              (assoc :headers {"Content-Type" "text/html; charset=utf-8"}))
          (-> "public/error.html"
              io/resource
              io/input-stream
              response
              (assoc :headers {"Content-Type" "text/html; charset=utf-8"}))))
   (GET "/dictionary" request
        (let [out (ByteArrayOutputStream. 4096)
              writer (t/writer out :json)]
          (t/write writer (dict/get-dictionary (:params request)))
          {:body (.toString out)}))
   (GET "/showversion" request
        {:status 200
         :headers {"Content-Type" "text/plain"}
         :body (str (project-clj/get :name) " v" (project-clj/get :version))})
   (ANY "*" _
        (-> "public/index.html"
            io/resource
            io/input-stream
            response
            (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))))

(defn home-routes [endpoint]
  (wrap-reload #'my-routes))
