(ns latakia.components.db
  (:require [clojure.java.io :as io]
            [cognitect.transit :as transit]
            [latakia.config :as config]
            [immuconf.config :as immu]
            [buddy.core.crypto :as crypto]
            [buddy.core.codecs :as codecs]
            [buddy.core.nonce :as nonce]))

(def pending-requests (atom {}))

(def key32 nil)

(defn exists-db? []
  (.exists (io/file
            (immu/get (config/config) :db-location))))

(defn write-db! []
  (let [config (config/config)
        backup-db (str (immu/get config :db-location) ".tmp")]
    (when (exists-db?)
      (io/copy
       (io/file (immu/get config :db-location))
       (io/file backup-db)))
    (with-open [o (clojure.java.io/output-stream (immu/get config :db-location))]
      (let [writer (transit/writer o :json)]
        (transit/write writer [@pending-requests])))
    (when (.exists (io/file backup-db)) (io/delete-file backup-db))))

(defn load-db! []
  (when-not (exists-db?) (write-db!))
  (with-open [i (clojure.java.io/input-stream (immu/get (config/config) :db-location))]
    (let [reader (transit/reader i :json)
          db (transit/read reader)]
      (reset! pending-requests (first db)))))
