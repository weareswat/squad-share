(ns squad-share.test.interactors.save-link
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go <!!]]
            [postgres.async :as pg]
            [squad-share.config :as config]
            [squad-share.interactors.save-link :as save-link]))

(deftest add-link
  (let [title (str (java.util.UUID/randomUUID))
        link {:title title :description "Description" :url "http://test"}
        context {}
        result (save-link/run! context link)]
        (prn result)
    ;; expecting result to be {:success true :link {... saved link data}}
    (is (true? (:success result)))

    (testing "the link is actually on the db"
      (let [result (<!! (pg/execute! config/db ["select * from squadshare.links where title=$1" title]))]
        (is (= title (-> result :rows first :title)))))))
