(ns squad-share.interactors.list-links
     (:require
       [clojure.core.async :refer [go <!!]]
       [postgres.async :as pg]
       [squad-share.config :as config]))

(defn run!
  [context]
  (let [ result (<!! (pg/execute! config/db ["select title, url, description from squadshare.links"])) ]
    (prn result)
    (result :rows)
  )
)
