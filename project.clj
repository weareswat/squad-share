(defproject weareswat/squad-share "0.1.0-SNAPSHOT"

  :description "Share web stuff with your team"
  :url "https://github.com/weareswat/squad-share"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [selmer "1.0.4"]
                 [markdown-clj "0.9.87"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [bouncer "1.0.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.2"]
                 [org.webjars/font-awesome "4.5.0"]
                 [org.webjars.bower/tether "1.1.1"]
                 [org.webjars/jquery "2.2.2"]
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.5.0"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-defaults "0.2.0"]
                 [mount "0.1.10"]
                 [cprop "0.1.7"]
                 [org.clojure/tools.cli "0.3.3"]
                 [luminus-nrepl "0.1.4"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [luminus-immutant "0.1.9"]
                 [luminus-log4j "0.1.3"]
                 [environ "1.0.2"]
                 [migratus "0.8.11"]
                 [alaisi/postgres.async "0.6.0"]
                 [postgresql "9.3-1102.jdbc41"]
                 [cheshire "5.6.1"]
                 [clj-time "0.11.0"]
                 [commons-validator "1.4.1"]]

  :aliases {"migrate"  ["run" "-m" "squad-share.migrations/migrate"]
            "rollback" ["run" "-m" "squad-share.migrations/rollback"]
            "autotest" ["test-refresh"]}

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj"]
  :resource-paths ["resources"]

  :main squad-share.core

  :plugins [[lein-cprop "1.0.1"]
            [com.jakemccrary/lein-test-refresh "0.14.0"]]
  :target-path "target/%s/"
  :profiles
  {:uberjar {:omit-source true

             :aot :all
             :uberjar-name "squad-share.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}
   :prod           [:project/prod]
   :project/prod {:resource-paths ["env/prod/resources" "env/prod/resources"]
                  :source-paths ["env/prod/clj"]}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[prone "1.1.1"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.8.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.14.0"]]


                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/dev/resources" "env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
