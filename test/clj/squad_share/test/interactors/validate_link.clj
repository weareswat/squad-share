(ns squad-share.test.interactors.validate-link
	(:require [clojure.test :refer :all]
						[clojure.core.async :refer [go <!!]]
						[postgres.async :as pg]
						[squad-share.config :as config]
						[squad-share.test.test-utils :as test-utils]
						[squad-share.interactors.validate-link :as validate-link]))

(use-fixtures :each test-utils/cleanup)

(deftest validate-link
	(testing "link-data with nil title/description/url"
		(let [link {}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title must be present"),
				;;	:url ("url must be present"),
				;;	:description ("description must be present")}
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (get (get result 0) :title)))
				(is (= '("url must be present") (get (get result 0) :url)))
				(is (= '("description must be present") (get (get result 0) :description)))
				;;	{:bouncer.core/errors
				;;	{:title ("title must be present"),
				;;	:url ("url must be present"),
				;;	:description ("description must be present")}}]))
				(is (= '("title must be present") (get (get (get result 1) :bouncer.core/errors) :title)))
				(is (= '("url must be present") (get (get (get result 1) :bouncer.core/errors) :url)))
				(is (= '("description must be present") (get (get (get result 1) :bouncer.core/errors) :description)))))

	(testing "link-data with nil title"
		(let [description "Description"
					url "http://www.test.com"
					link {:description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title must be present")}
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (get (get result 0) :title)))
				;;	{:description "Description",
				(is (= description (get (get result 1) :description)))
				;;	:url "http://www.test.com",
				(is (= url (get (get result 1) :url)))
				;;	:bouncer.core/errors
				;;	{:title ("title must be present")}}]
				(is (= '("title must be present") (get (get (get result 1) :bouncer.core/errors) :title)))))

	(testing "link-data with nil description"
		(let [title (str (java.util.UUID/randomUUID))
					url "http://www.test.com"
					link {:title title :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:description ("description must be present")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("description must be present") (get (get result 0) :description)))
				;;	{:title randomUUID,
				(is (= title (get (get result 1) :title)))
				;;	:url "http://www.test.com",
				(is (= url (get (get result 1) :url)))
				;;	:bouncer.core/errors
				;;	{:description ("description must be present")}}]
				(is (= '("description must be present") (get (get (get result 1) :bouncer.core/errors) :description)))))

	(testing "link-data with nil url"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					link {:title title :description description}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:url ("url must be present")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("url must be present") (get (get result 0) :url)))
				;;	{:title randomUUID,
				(is (= title (get (get result 1) :title)))
				;;	:description "Description",
				(is (= description (get (get result 1) :description)))
				;;	:bouncer.core/errors
				;;	{:url ("url must be present")}}]
				(is (= '("url must be present") (get (get (get result 1) :bouncer.core/errors) :url)))))

	(testing "link-data with empty title"
		(let [title ""
					description "Description"
					url "http://www.test.com"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title can't be empty")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (get (get result 0) :title)))
				;;	{:description "Description",
				(is (= description (get (get result 1) :description)))
				;;	:url "http://www.test.com",
				(is (= url (get (get result 1) :url)))
				;;	:bouncer.core/errors
				;;	{:title ("title can't be empty")}}]
				(is (= '("title must be present") (get (get (get result 1) :bouncer.core/errors) :title)))))

	(testing "link-data with empty description"
		(let [title (str (java.util.UUID/randomUUID))
					description ""
					url "http://www.test.com"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:description ("description can't be empty")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("description must be present") (get (get result 0) :description)))
				;;	{:title randomUUID,
				(is (= title (get (get result 1) :title)))
				;;	:url "http://www.test.com",
				(is (= url (get (get result 1) :url)))
				;;	:bouncer.core/errors
				;;	{:description ("description can't be empty")}}]
				(is (= '("description must be present") (get (get (get result 1) :bouncer.core/errors) :description)))))

	(testing "link-data with invalid url"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					url "http://test"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:url ("url invalid")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("url invalid") (get (get result 0) :url)))
				;;	{:title randomUUID,
				(is (= title (get (get result 1) :title)))
				;;	:description "Description",
				(is (= description (get (get result 1) :description)))
				;;	:bouncer.core/errors
				;;	{:url ("url invalid")}}]
				(is (= '("url invalid") (get (get (get result 1) :bouncer.core/errors) :url)))))

	(testing "link-data with nil errors"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					url "http://www.test.com"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
					;; expecting result to be [nil link]
				(is (nil? (get result 0)))
				(is (= title (get (get result 1) :title)))
				(is (= description (get (get result 1) :description)))
				(is (= url (get (get result 1) :url))))))