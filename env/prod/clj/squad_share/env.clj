(ns squad-share.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[squad-share started successfully]=-"))
   :middleware identity})
