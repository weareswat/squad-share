(ns squad-share.test.test-utils
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go <!!]]
            [ring.mock.request :refer :all]
            [postgres.async :as pg]
            [squad-share.config :refer [env db]]
            [squad-share.handler :refer :all]))

(defn cleanup
  [f]
  (f)
  (<!! (pg/execute! db ["delete from squadshare.links;"])))
