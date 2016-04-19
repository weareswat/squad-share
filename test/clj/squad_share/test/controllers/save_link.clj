(ns squad-share.test.controllers.save-link
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]
            [squad-share.handler :as handler]
            [cheshire.core :refer :all]))

(deftest save-link-route
  (testing "save link route"
    (let [title (str (java.util.UUID/randomUUID))
          link {:title title :description "Description" :url "http://test"}
          link_json (generate-string link)
          response ((handler/app) (request :post "/save-link.json" link_json))
          body (parse-string (:body response) true)
          ]
      (is (= 201 (:status response)))
      (is body)
      (testing "the link is actually on the db"
        (let [result (<!! (pg/execute! config/db ["select * from squadshare.links where title=$1" title]))]
          (is (= title (-> result :rows first :title)))))
      (testing "the response has the link "
        (is (= title (:title body))))
)))
