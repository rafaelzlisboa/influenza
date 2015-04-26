(ns influenza.social-graph
  (:gen-class)
  (:import clojure.lang.PersistentQueue)
  (:require [clojure.string :as string]))

(defn- new-paths [graph path node visited]
  (filter #(not (visited (first %)))
          (map #(cons % path) (graph node))))

(defn- bfs [graph queue done? visited]
  (if-not (seq queue) nil
    (let [path (first queue)
          node (first path)
          new-visited (conj visited node)]
      (if (done? node) (reverse path)
        (recur graph
               (concat (rest queue)
                       (new-paths graph path node new-visited))
               done?
               new-visited)))))

(defn- shortest-path [graph start end]
  (bfs graph (conj PersistentQueue/EMPTY (list start)) #(= %1 end) #{}))

(defn add-connection
  [graph [person-this person-that]]
  (-> graph
      (update-in [person-this] conj person-that)
      (update-in [person-that] conj person-this)))

(defn social-distance [path]
  (if (nil? path) 0
    (dec (count path))))

(defn- farness [social-graph person]
  (reduce + (map #(social-distance (shortest-path social-graph person %))
                 (remove #{person} (keys social-graph)))))

(defn closeness [social-graph person]
  (let [farness (farness social-graph person)]
    (if (zero? farness) 0
      (/ 1 farness))))

(defn- all-connections-from [social-graph persons]
  (remove nil? (flatten (for [person persons] (social-graph person)))))

(defn- zero-score [scores person]
  (assoc scores person 0))

(defn- exp [base exp] (apply * (repeat exp base)))

(defn- decrease-score-fn [level]
  (let [fraud-coefficient (- 1 (exp 1/2 level))]
    (fn [scores person]
      (update-in scores [person] * fraud-coefficient))))

(defn process-frauds [social-graph scores frauds]
  (let [score-zeroed-frauds (reduce zero-score scores frauds)]
    (loop [connection-level 1
           this-level-connections (all-connections-from social-graph frauds)
           current-scores (reduce (decrease-score-fn connection-level)
                                  score-zeroed-frauds
                                  this-level-connections)]
      (if (and (seq this-level-connections)
               (< 10 connection-level))
        (recur
          (inc connection-level)
          (all-connections-from social-graph this-level-connections)
          (reduce (decrease-score-fn connection-level)
                   current-scores
                   this-level-connections))
        current-scores))))

(defn rank-influence
  ([social-graph] (rank-influence social-graph []))
  ([social-graph frauds]
    (let [scores (zipmap (keys social-graph)
                         (map #(closeness social-graph %)
                              (keys social-graph)))]
      (sort-by val > (process-frauds social-graph scores frauds)))))

(defn- process-line [line]
  (map keyword (string/split line #" ")))

(defn load-social-graph [file-contents]
  (let [graph {}]
    (reduce add-connection graph (map process-line (string/split-lines file-contents)))))
