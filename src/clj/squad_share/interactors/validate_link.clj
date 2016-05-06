(ns squad-share.interactors.validate-link
	(:refer-clojure :exclude [run!])
	(:require
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

	(bouncer_core/validate link-data
		:title validators/required
		:url [validators/required valid-url]
		:description validators/required))