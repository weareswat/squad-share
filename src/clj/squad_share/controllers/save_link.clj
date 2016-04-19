(ns squad-share.controllers.save-link
  (:require [cheshire.core :refer :all]
            [squad-share.interactors.save-link :as save-link]))

(defn handler
  [request]
  (let [body (slurp (:body request))
       link (parse-string body)
       result (save-link/run! {} link)]
       (if (:success result)
          {:status 201 :body (generate-string (:link result))}
          {:status 500}
        )
))
