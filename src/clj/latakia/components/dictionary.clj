(ns latakia.components.dictionary
  (:require [latakia.config :as config]
            [immuconf.config :as immu]
            [clojure.string :as s]))

(defn get-dictionary [params]
  (:dictionaries (config/config)))

(defn get-favourite-language [request]
  (val
   (first
    (reduce
     #(let [[l q] (s/split %2 #";")]
        (assoc %1 (if (nil? q) 1.0 (Float/parseFloat(last (s/split q #"=")))) l))
     (sorted-map-by >)
     (s/split (get (:headers request) "accept-language") #"\,")))))
