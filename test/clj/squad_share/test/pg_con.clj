(ns squad-share.test.pg-con
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go <!!]]
            [ring.mock.request :refer :all]
            [postgres.async :as pg]
            [squad-share.config :refer [env db]]
            [squad-share.handler :refer :all]))

(deftest pg-connection-is-up
  (let [result (<!! (pg/execute! db ["select 123 as num;"]))]
    (is (= 123 (-> result :rows first :num)))))
