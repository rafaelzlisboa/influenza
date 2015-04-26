(ns influenza.score-calc.frauds
  (:require [clojure.set :as set])
  (:gen-class))

(defn- all-connections-from [social-graph persons]
  (distinct (remove nil? (flatten (for [person persons] (social-graph person))))))

(defn- zero-score [scores person]
  (assoc scores person 0))

(defn- exp [base exp] (apply * (repeat exp base)))

(defn- decrease-score-fn [level]
  (let [fraud-coefficient (- 1 (exp 1/2 level))]
    (fn [scores person]
      (update-in scores [person] * fraud-coefficient))))

(defn- without [coll1 coll2]
  (filter (complement (into #{} coll2)) coll1))

(defn penalize-frauds [social-graph scores frauds]
  (let [score-zeroed-frauds (reduce zero-score scores frauds)]
    (loop [connection-level 1
           already-penalized frauds
           this-level-connections (without (all-connections-from social-graph frauds)
                                           already-penalized)
           current-scores (reduce (decrease-score-fn connection-level)
                                  score-zeroed-frauds
                                  this-level-connections)]
      (if (seq this-level-connections)
        (recur
         (inc connection-level)
         (distinct (into already-penalized this-level-connections))
         (without (all-connections-from social-graph this-level-connections)
                  (distinct (into already-penalized this-level-connections)))
         (reduce (decrease-score-fn (inc connection-level))
                 current-scores
                 (without (all-connections-from social-graph this-level-connections)
                          already-penalized)))
        current-scores))))
