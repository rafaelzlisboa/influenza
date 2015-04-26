(ns influenza.social-graph-test
  (:require [expectations :refer :all]
            [influenza.social-graph :refer :all]
            [clojure.string]))

(expect {:1 [:2]
         :2 [:1]}
  (-> {}
      (add-connection [:1 :2])))

(expect {:1 [:3 :2]
         :2 [:1]
         :3 [:1]} (load-social-graph "1 2\n3 1\n"))

(expect 0 (social-distance nil))
(expect 0 (social-distance [:1]))
(expect 1 (social-distance [:1 :2]))
(expect 2 (social-distance [:1 :2 :3]))


;; 1  7
;; |\
;; 2 3-6
;; |/|/
;; 4 5
(def a-social-graph (-> {}
                        (add-connection [:1 :2])
                        (add-connection [:1 :3])
                        (add-connection [:2 :4])
                        (add-connection [:4 :3])
                        (add-connection [:3 :5])
                        (add-connection [:3 :6])
                        (add-connection [:5 :6])))

(expect 1/9 (closeness a-social-graph :6))
(expect 0 (closeness a-social-graph :7))

(expect [:3 1/6] (first (rank-influence a-social-graph)))

(let [fraudulents [:3]
      fraud-aware-scores (rank-influence a-social-graph
                                         fraudulents)]
  ;; a fraudulent person's score is 0
  (expect [:3 0] (last fraud-aware-scores))
  ;; a fraudulent person friend's score is halved
  (expect (some #{[:6 (/ 1/9 2)]} fraud-aware-scores)))
