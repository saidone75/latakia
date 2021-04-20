(ns latakia.components.crypto
  (:require [lock-key.core :as lock]
            [secrets.core :as secrets]))

(def ^:const secret (secrets/token-urlsafe 32))

(defn encrypt [s]
  (lock/encrypt-as-base64 s secret))

(defn decrypt [s]
  (lock/decrypt-from-base64 s secret))
