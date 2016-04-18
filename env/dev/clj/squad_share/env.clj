(ns squad-share.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [squad-share.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[squad-share started successfully using the development profile]=-"))
   :middleware wrap-dev})
