(ns squad-share.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [squad-share.handler :as handler]))

(deftest test-app
  (testing "main route"
    (let [response ((handler/app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((handler/app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))
