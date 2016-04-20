(ns squad-share.test.controllers.save-link
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]
            [squad-share.handler :as handler]
            [squad-share.test.test-utils :as test-utils]
            [cheshire.core :refer :all]))

(use-fixtures :each test-utils/cleanup)

(deftest save-link-json-route
  (testing "save link json route"
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

(deftest save-link-route
  (testing "save link route"
    (let [title (str (java.util.UUID/randomUUID))
          link (str "?description=description&url=http://test&title=" title)
          response ((handler/app) (request :post (str "/save-link" link) ""))
          ]
      (is (= 301 (:status response)))
      (is (get-in response [:headers "Location"]))
      (testing "the link is actually on the db"
        (let [result (<!! (pg/execute! config/db ["select * from squadshare.links where title=$1" title]))]
          (is (= title (-> result :rows first :title))))))))
