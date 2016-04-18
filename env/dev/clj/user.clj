(ns user
  (:require [mount.core :as mount]
            squad-share.core))

(defn start []
  (mount/start-without #'squad-share.core/repl-server))

(defn stop []
  (mount/stop-except #'squad-share.core/repl-server))

(defn restart []
  (stop)
  (start))


