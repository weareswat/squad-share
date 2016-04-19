(ns squad_share.interactors.list-all-links
     (:require
       [clojure.core.async :refer [go <!!]]
       [postgres.async :as pg]
       [squad-share.config :as config]))

(defn run!
      [context]
      (let [result (<!! (pg/select config/db {:table "squadshare.links"}))]
           )

      )
