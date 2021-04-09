(ns latakia.config
  (:require [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [immuconf.config :as immu]))

(defn config []
  (merge
   {:http-port  (Integer. (or (env :port) 10555))
    :middleware [[wrap-defaults api-defaults]
                 wrap-with-logger
                 wrap-gzip]}
   (immu/load "latakia-cfg.edn")))
