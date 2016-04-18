(ns squad-share.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [postgres.async :as pg]
            [mount.core :refer [args defstate]]))

(defn get-config
  []
(load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)]))

(defstate env :start (get-config))

(def config (-> (get-config) :pg-conn))
(def db (pg/open-db config))

