(defproject influenza "0.1.0-SNAPSHOT"
  :description "see the influence of users in a social network"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.3.3"]
                 [expectations "2.1.1"]
                 [ring/ring-mock "0.2.0"]]
  :ring {:handler influenza.core/app}
  :plugins [[lein-autoexpect "1.0"]
            [lein-ring "0.8.7"]])
