(ns squad-share.interactors.save-link
  (:require
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]))

(defn run!
  [context link-data]
  (let [ result (<!! (pg/insert! config/db {:table "squadshare.links"} link-data))]
    (if (= 1 (:updated result))
      {:success true}
      {:success false}
    )
  )
)
