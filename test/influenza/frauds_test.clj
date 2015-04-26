(ns influenza.frauds-test
  (:require [expectations :refer :all]
            [influenza.social-graph :refer :all]
            [influenza.frauds :refer :all]))


;; 1-2-3f
;;   4-5
(def a-social-graph (-> {}
                        (add-connection [:1 :2])
                        (add-connection [:2 :3])
                        (add-connection [:4 :5])))
(def some-scores {:1 60 :2 50 :3 100
                  :4 20 :5 30})
(def fraudulents [:3])

(let [new-scores (process-frauds a-social-graph
                                 some-scores
                                 fraudulents)]
  ;; a fraudulent person's score is 0
  (expect 0 (new-scores :3))

  ;; a fraudulent person friend's score is halved
  (expect 25 (new-scores :2))

  ;; a fraudulent person indirect connection's score is also decreased
  (expect 45 (new-scores :1))

  ;; others on the network are not affected
  (expect 20 (new-scores :4))
  (expect 30 (new-scores :5)))
