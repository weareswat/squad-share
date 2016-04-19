(ns squad-share.test.interactors.list-all-links
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]
            [squad-share.interactors.list-all-links :as list-all-links]))

(deftest list-all-links
  ((<!! (pg/insert! config/db {:table "squadshare.links"} {:title (str (java.util.UUID/randomUUID)), :description "Description", :url "http://test"}))
   (let [ context {}
          result (list-all-links/run! context)]
        (prn result)

    (testing "the link is retreived by the interactor"
      (let [result (<!! (pg/execute! config/db ["select count(title) from squadshare.links"]))]
        (= result 1))))))
