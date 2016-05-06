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
	(testing "call link json route without title"
		(let [link {:description "Description" :url "http://www.test.com"}
			link_json (generate-string link)
			response ((handler/app) (request :post "/save-link.json" link_json))
			body (parse-string (:body response) true)]
			(is (= 400 (:status response)))
			(is (= '{:title ["title must be present"]} body))))

	(testing "call link json route without url"
		(let [title (str (java.util.UUID/randomUUID))
			link {:title title :description "Description"}
			link_json (generate-string link)
			response ((handler/app) (request :post "/save-link.json" link_json))
			body (parse-string (:body response) true)]
			(is (= 400 (:status response)))
			(is (= '{:url ["url must be present"]} body))))

	(testing "call link json route with invalid url"
		(let [title (str (java.util.UUID/randomUUID))
			link {:title title :description "Description" :url "http://test"}
			link_json (generate-string link)
			response ((handler/app) (request :post "/save-link.json" link_json))
			body (parse-string (:body response) true)]
			(is (= 400 (:status response)))
			(is (= '{:url ["url invalid"]} body))))

	(testing "save link json route"
		(let [title (str (java.util.UUID/randomUUID))
			link {:title title :description "Description" :url "http://www.test.com"}
			link_json (generate-string link)
			response ((handler/app) (request :post "/save-link.json" link_json))
			body (parse-string (:body response) true)]
			(is (= 201 (:status response)))
			(is body)
			(testing "the link is actually on the db"
				(let [result (<!! (pg/execute! config/db ["select * from squadshare.links where title=$1" title]))]
					(is (= title (-> result :rows first :title)))))
			(testing "the response has the link "
				(is (= title (:title body)))))))

(deftest save-link-route
	(testing "save link route without title"
		(let [link (str "?description=description&url=http://www.test.com")
			response ((handler/app) (request :post (str "/save-link" link) ""))]
			(is (= 400 (:status response)))
			(is (= '{:title ["title must be present"]} (parse-string (:body response) true)))))

	(testing "save link route without url"
		(let [title (str (java.util.UUID/randomUUID))
			link (str "?description=description&title=" title)
			response ((handler/app) (request :post (str "/save-link" link) ""))]
			(is (= 400 (:status response)))
			(is (= '{:url ["url must be present"]} (parse-string (:body response) true)))))

	(testing "save link route with invalid url"
		(let [title (str (java.util.UUID/randomUUID))
			link (str "?description=description&url=http://test&title=" title)
			response ((handler/app) (request :post (str "/save-link" link) ""))]
			(is (= 400 (:status response)))
			(is (= '{:url ["url invalid"]} (parse-string (:body response) true)))))

	(testing "save link route"
		(let [title (str (java.util.UUID/randomUUID))
			link (str "?description=description&url=http://www.test.com&title=" title)
			response ((handler/app) (request :post (str "/save-link" link) ""))]
			(is (= 301 (:status response)))
			(is (get-in response [:headers "Location"]))
			(testing "the link is actually on the db"
				(let [result (<!! (pg/execute! config/db ["select * from squadshare.links where title=$1" title]))]
					(is (= title (-> result :rows first :title))))))))
