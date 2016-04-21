(ns squad-share.routes.home
  (:require [squad-share.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [squad-share.controllers.save-link :as save-link]
            [squad-share.controllers.list-links :as list-links]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (list-links/handler request))
  (GET "/about" [] (about-page))
  (GET "/links" request (list-links/handler request))
  (GET "/save-link" request (save-link/handler-html request))
  (POST "/save-link.json" request (save-link/handler-json request))
  (POST "/save-link" request (save-link/handler request)))
