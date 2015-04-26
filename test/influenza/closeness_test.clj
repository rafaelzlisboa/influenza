(ns influenza.closeness-test
  (:require [expectations :refer :all]
            [influenza.closeness :refer :all]
            [influenza.social-graph :refer :all]))

;; 1  7
;;  \
;; 2 3
;; |/
;; 4
(def a-social-graph (-> {}
                        (add-connection [:1 :3])
                        (add-connection [:2 :4])
                        (add-connection [:4 :3])))

(expect 1/6 (closeness a-social-graph :1))
(expect 1/6 (closeness a-social-graph :2))
(expect 1/4 (closeness a-social-graph :3))
(expect 1/4 (closeness a-social-graph :4))
(expect 0 (closeness a-social-graph :7))
