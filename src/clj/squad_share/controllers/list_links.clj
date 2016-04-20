(ns squad-share.controllers.list-links
  (:require [squad-share.layout :as layout]
            [squad-share.interactors.list-links :as list-links]))

(defn handler
  [request]
  (let [result (list-links/run! {})]
    (layout/render "links.html" {:links result}))
)
