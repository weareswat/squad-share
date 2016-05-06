(ns squad-share.test.interactors.list-links
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]
            [squad-share.test.test-utils :as test-utils]
            [squad-share.interactors.list-links :as list-links]))

(use-fixtures :each test-utils/cleanup)

(deftest list-links
   (let [ 
          _ (<!! (pg/insert! config/db {:table "squadshare.links"} {:title (str (java.util.UUID/randomUUID)), :description "Description", :url "http://www.test.com"}))
          _ (<!! (pg/insert! config/db {:table "squadshare.links"} {:title (str (java.util.UUID/randomUUID)), :description "Description", :url "http://www.test.com"}))
          context {}
          result (list-links/run! context)]
    (testing "the link is retreived by the interactor"
      (let [result (<!! (pg/execute! config/db ["select * from squadshare.links"]))]
        ( is (= (count (-> result
                           :rows
                           )) 2 ))))))
