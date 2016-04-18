(ns squad-share.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [postgres.async :as pg]
            [mount.core :refer [args defstate]]))

(defstate env :start (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)]))

(def config (read-string (slurp "env/dev/resources/config.edn")))
(def db (pg/open-db (-> config :pg-conn)))

