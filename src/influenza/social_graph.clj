(ns influenza.social-graph
  (:gen-class)
  (:import clojure.lang.PersistentQueue))

(defn new-paths
  [graph path node visited]
  (filter #(not (visited (first %)))
          (map #(cons % path) (graph node))))

(defn bfs
  [graph queue done? visited]
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

(defn shortest-path
  [graph start end]
  (bfs graph (conj PersistentQueue/EMPTY (list start)) #(= %1 end) #{}))

(defn add-person
  [graph person]
  (assoc graph person []))

(defn add-connection
  [graph person-this person-that]
  (-> graph
      (update-in [person-this] conj person-that)
      (update-in [person-that] conj person-this)))

(defn social-distance
  [path]
  (if (nil? path) 0
    (dec (count path))))

(defn farness
  [social-graph person]
  (reduce + (map #(social-distance (shortest-path social-graph person %))
                 (remove #{person} (keys social-graph)))))

(defn closeness
  [social-graph person]
  (let [farness (farness social-graph person)]
    (if (= 0 farness) 0
      (/ 1 farness))))

(defn rank-centrality
  [social-graph]
  (let [scores (zipmap (keys social-graph)
                       (map #(closeness social-graph %)
                            (keys social-graph)))]
    (sort-by val > scores)))
