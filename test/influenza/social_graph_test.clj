(ns influenza.social-graph-test
  (:require [expectations :refer :all]
            [influenza.social-graph :refer :all]))

(expect {:1 []} (add-person {} :1))

(expect {:1 [:2]
         :2 [:1]}
  (-> {}
      (add-person :1)
      (add-person :2)
      (add-connection :1 :2)))


;; 1  7
;; |\
;; 2 3-6
;; |/|/
;; 4 5

(def a-social-graph (-> {}
                        (add-person :1)
                        (add-person :2)
                        (add-person :3)
                        (add-person :4)
                        (add-person :5)
                        (add-person :6)
                        (add-person :7)
                        (add-connection :1 :2)
                        (add-connection :1 :3)
                        (add-connection :2 :4)
                        (add-connection :4 :3)
                        (add-connection :3 :5)
                        (add-connection :3 :6)
                        (add-connection :5 :6)))

(expect 0 (social-distance nil))
(expect 0 (social-distance [:1]))
(expect 1 (social-distance [:1 :2]))
(expect 2 (social-distance [:1 :2 :3]))

(expect 1/9 (closeness a-social-graph :6))

(expect [:3 1/6] (first (rank-centrality a-social-graph)))
