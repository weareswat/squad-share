(ns squad-share.interactors.validate-link
	(:refer-clojure :exclude [run!])
	(:require
			[clojure.test :refer :all]
			[clojure.core.async :refer [go <!!]]
			[postgres.async :as pg]
			[squad-share.config :as config]
			[bouncer.core :as bouncer_core]
			[bouncer.validators :as validators]))

;; http://stackoverflow.com/questions/28269117/clojure-regex-if-string-is-a-url-return-string
(import 'org.apache.commons.validator.UrlValidator)
(defn valid-url? [url-str]
  (let [validator (UrlValidator.)]
    (.isValid validator url-str)))

;; https://github.com/leonardoborges/bouncer#basic-validations
(defn run!
	"Validate received link-data params"
	[link-data]

	(validators/defvalidator valid-url
		{:default-message-format "%s invalid"}
		[url]
		(valid-url? url))
	
	(validators/defvalidator unique-url
		{:default-message-format "%s already exists"}
		[url]
		(let [result (<!! (pg/execute! config/db ["select * from squadshare.links where url=$1" url]))]
			(= (count (get result :rows)) 0)))

	(bouncer_core/validate link-data
		:title validators/required
		:url [validators/required valid-url unique-url]))