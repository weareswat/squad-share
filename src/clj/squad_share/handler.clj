(ns squad-share.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [squad-share.layout :refer [error-page]]
            [squad-share.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [squad-share.middleware :as middleware]))

(def app-routes
  (routes
    home-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
