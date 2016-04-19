(ns squad-share.migrations
  (:require [migratus.core :as migratus]
            [squad-share.config :as config]))

(def config {:store                :database
             :migration-dir        "migrations/"
             :migration-table-name "migrations"
             :db config/dbconfig})

(defn migrate [& args]
  (migratus/migrate config))

(defn rollback [& args]
  (migratus/rollback config))
