(ns latakia.components.prosody
  (:require [me.raynes.conch :refer [let-programs] :as sh]
            [latakia.config :as config]
            [immuconf.config :as immu]))

(defn adduser [email password]
  (let-programs [prosodyctl-adduser (or (immu/get (config/config) :prosodyctl-adduser) "prosodyctl-adduser.sh")]
    (binding [sh/*throw* false]
      (= 0 @(:exit-code (prosodyctl-adduser email password {:verbose true}))))))

(defn passwd [email password]
  (let-programs [prosodyctl-passwd (or (immu/get (config/config) :prosodyctl-passwd) "prosodyctl-passwd.sh")]
    (binding [sh/*throw* false]
      (= 0 @(:exit-code (prosodyctl-passwd email password {:verbose true}))))))
