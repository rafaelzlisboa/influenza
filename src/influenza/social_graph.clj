(ns influenza.social-graph
  (:gen-class)
  (:require [clojure.string :as string]
            [influenza.score-calc.closeness :refer :all]
            [influenza.score-calc.frauds :refer :all]))

(defn add-connection
  [graph [person-this person-that]]
  (-> graph
      (update-in [person-this] conj person-that)
      (update-in [person-that] conj person-this)))

(defn rank-influence
  ([social-graph] (rank-influence social-graph []))
  ([social-graph frauds]
    (let [scores (zipmap (keys social-graph)
                         (map #(closeness social-graph %)
                              (keys social-graph)))]
      (sort-by val > (penalize-frauds social-graph scores frauds)))))

(defn- process-line [line]
  (map keyword (string/split line #" ")))

(defn load-social-graph [file-contents]
  (let [graph {}]
    (reduce add-connection graph (map process-line (string/split-lines file-contents)))))
