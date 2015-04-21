(ns influenza.core
  (:gen-class)
  (:require [influenza.social-graph :refer :all]
            [clojure.java.io :as io]))

(def ^:private file-data
  (-> (io/resource "edges")
      (io/file)
      (slurp)))

(defn -main []
  (println "social influence rankings")
  (println (rank-influence (load-social-graph file-data))))
