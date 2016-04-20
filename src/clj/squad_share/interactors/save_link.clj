(ns squad-share.interactors.save-link
  (:refer-clojure :exclude [run!])
  (:require
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [clj-time.core :as time]
            [clj-time.coerce :as c]
            [squad-share.config :as config]))

(defn run!
  "Save link on DB"
  [context link-data]
  (let [ link-with-date (assoc link-data :created_at (c/to-string (time/now)))
        result (<!! (pg/insert! config/db {:table "squadshare.links" :returning "id"} link-with-date ))
        id (-> result :rows first :id)]
    (prn result)
    (if (= 1 (:updated result))
      {:success true :link (assoc link-with-date :id id)}
      {:success false}
    )
  )
)
