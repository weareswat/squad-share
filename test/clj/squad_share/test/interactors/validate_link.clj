(ns squad-share.test.interactors.validate-link
	(:require [clojure.test :refer :all]
						[clojure.core.async :refer [go <!!]]
						[postgres.async :as pg]
						[clj-time.core :as time]
						[clj-time.coerce :as c]
						[squad-share.config :as config]
						[squad-share.test.test-utils :as test-utils]
						[squad-share.interactors.validate-link :as validate-link]))

(use-fixtures :each test-utils/cleanup)

(deftest validate-link
	(testing "link-data with nil title/url"
		(let [link {}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title must be present"),
				;;	:url ("url must be present"),
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (:title (get result 0))))
				(is (= '("url must be present") (:url (get result 0))))
				;;	{:bouncer.core/errors
				;;	{:title ("title must be present"),
				;;	:url ("url must be present"),
				(is (= '("title must be present") (:title (:bouncer.core/errors (get result 1)))))
				(is (= '("url must be present") (:url (:bouncer.core/errors (get result 1)))))))

	(testing "link-data with nil title"
		(let [description "Description"
					url "http://www.no.existing.url.com"
					link {:description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title must be present")}
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (:title (get result 0))))
				;;	{:description "Description",
				(is (= description (:description (get result 1))))
				;;	:url "http://www.test.com",
				(is (= url (:url (get result 1))))
				;;	:bouncer.core/errors
				;;	{:title ("title must be present")}}]
				(is (= '("title must be present") (:title (:bouncer.core/errors (get result 1)))))))

	(testing "link-data with nil url"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					link {:title title :description description}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:url ("url must be present")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("url must be present") (:url (get result 0))))
				;;	{:title randomUUID,
				(is (= title (:title (get result 1))))
				;;	:description "Description",
				(is (= description (:description (get result 1))))
				;;	:bouncer.core/errors
				;;	{:url ("url must be present")}}]
				(is (= '("url must be present") (:url (:bouncer.core/errors (get result 1)))))))

	(testing "link-data with empty title"
		(let [title ""
					description "Description"
					url "http://www.no.existing.url.com"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:title ("title can't be empty")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("title must be present") (:title (get result 0))))
				;;	{:description "Description",
				(is (= description (:description (get result 1))))
				;;	:url "http://www.test.com",
				(is (= url (:url (get result 1))))
				;;	:bouncer.core/errors
				;;	{:title ("title can't be empty")}}]
				(is (= '("title must be present") (:title (:bouncer.core/errors (get result 1)))))))

	(testing "link-data with invalid url"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					url "http://www.invalid-url"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
				;; expecting result to be:
				;; [{:url ("url invalid")} 
				(is ((complement nil?) (get result 0)))
				(is (= '("url invalid") (:url (get result 0))))
				;;	{:title randomUUID,
				(is (= title (:title (get result 1))))
				;;	:description "Description",
				(is (= description (:description (get result 1))))
				;;	:bouncer.core/errors
				;;	{:url ("url invalid")}}]
				(is (= '("url invalid") (:url (:bouncer.core/errors (get result 1)))))))

	(testing "link-data with existing url"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					url "http://www.existing.url.com"
					link {:title title :description description :url url}
					link-with-date (assoc link :created_at (c/to-string (time/now)))
					insert-result (<!! (pg/insert! config/db {:table "squadshare.links" :returning "id"} link-with-date ))
					id (-> insert-result :rows first :id)]
				(if (= 1 (:updated insert-result))
					(let [validate-result (validate-link/run! link)
								delete-result (<!! (pg/execute! config/db ["delete from squadshare.links where id=$1" id]))]
								(is (= (get delete-result :updated) 1))
								;; expecting validate-result to be:
								;; [{:url (url already exists)}
								(is ((complement nil?) (get validate-result 0)))
								(is (= '("url already exists") (:url (get validate-result 0))))
								;;	{:title ...,
								(is (= title (:title (get validate-result 1))))
								;;	{:description Description,
								(is (= description (:description (get validate-result 1))))
								;;	:url http://www.test.com,
								(is (= url (:url (get validate-result 1))))
								;;	:bouncer.core/errors
								;;	{:url (url already exists)}}]
								(is (= '("url already exists") (:url (:bouncer.core/errors (get validate-result 1)))))
					))))

	(testing "link-data with nil errors"
		(let [title (str (java.util.UUID/randomUUID))
					description "Description"
					url "http://www.no.existing.url.com"
					link {:title title :description description :url url}
					result (validate-link/run! link)]
					;; expecting result to be [nil link]
				(is (nil? (get result 0)))
				(is (= title (:title (get result 1))))
				(is (= description (:description (get result 1))))
				(is (= url (:url (get result 1)))))))