(defproject influenza "0.1.0-SNAPSHOT"
  :description "see the influence of users in a social network"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [expectations "1.4.52"]]
  :main ^:skip-aot influenza.core
  :target-path "target/%s"
  :plugins [[lein-gorilla "0.3.4"]
            [lein-autoexpect "1.0"]
            [lein-ancient "0.6.6"]]
  :profiles {:uberjar {:aot :all}})
