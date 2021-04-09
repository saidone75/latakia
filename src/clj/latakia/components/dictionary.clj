(ns latakia.components.dictionary
  (:require [latakia.config :as config]
            [immuconf.config :as immu]))

(defn get-dictionary [params]
  (:dictionaries (config/config)))
