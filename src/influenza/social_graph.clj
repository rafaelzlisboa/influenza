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

(defn- add-person [graph person]
  (assoc graph person []))

(defn add-connection
  [graph [person-this person-that]]
  (if (nil? (graph person-this)) (add-person graph person-this))
  (if (nil? (graph person-that)) (add-person graph person-that))
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

(defn process-frauds [scores frauds]
  (reduce #(assoc %1 %2 0) scores frauds))

(defn rank-influence
  ([social-graph] (rank-influence social-graph []))
  ([social-graph frauds]
    (let [scores (zipmap (keys social-graph)
                         (map #(closeness social-graph %)
                              (keys social-graph)))]
      (sort-by val > (process-frauds scores frauds)))))

(defn- process-line [line]
  (map keyword (string/split line #" ")))

(defn load-social-graph [file-contents]
  (let [graph {}]
    (reduce add-connection graph (map process-line (string/split-lines file-contents)))))
