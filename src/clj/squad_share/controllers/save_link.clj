(ns squad-share.controllers.save-link
  (:require [cheshire.core :refer :all]
            [squad-share.layout :as layout]
            [clojure.string :as str]
            [squad-share.interactors.save-link :as save-link]))

(defn handler-json
  "Save link using JSON on body"
  [request]
  (let [body (slurp (:body request))
       link (parse-string body)
       result (save-link/run! {} link)]
       (if (:success result)
          {:status 201 :body (generate-string (:link result))}
          {:status 500}
        )))

(defn- add-new-key-value-param
  "Get key-value from param and merge with the accumulator"
  [accumulator, current]
  (let [split-result (str/split current #"=")
        key-value { (keyword(get split-result 0)) (get split-result 1) }]
    (merge accumulator key-value)))

(defn- parse-body
  "Get map with all params"
  [body]
  (let [params (str/split body #"&")]
    (reduce add-new-key-value-param {} params)))

(defn handler-raw-post
  "Save link using querystring format on body"
  [request]
  (let [body (slurp (:body request))
       link (parse-body body)
       result (save-link/run! {} link)]
       (if (:success result)
          {:status 301 :location "/"}
          {:status 500})))

(defn handler
  "Save link using querystring format on body"
  [request]
  (let [result (save-link/run! {} (:form-params request))
        id (get-in result [:link :id])]
     (if (:success result)
        {:status 301 :headers {"Location" (str "/?added=" id)}}
        {:status 500})))

(defn handler-html
  "Returns a web form to add a link"
  [request]
  (layout/render "save-link.html"))
