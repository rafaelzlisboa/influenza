(ns influenza.core-test
  (:use ring.mock.request)
  (:require [expectations :refer :all]
            [influenza.core :refer :all]
            [cheshire.core :as json]))

;; mocking the social graph
(reset! influenza-social-graph {:1 [:3 :2]
                                :2 [:1]
                                :3 [:1]})

;; GET /wrong-url
(let [response (app (request :get "/wrong-url"))]
  (expect 404 (:status response)))

;; GET /social-influence-ranking returns the ranking
(let [response (app (request :get "/social-influence-ranking"))]
  (expect 200 (:status response))
  (expect ["1" 0.5] (first (json/parse-string (:body response)))))

;; POST /persons creates a new person
(let [response (app (-> (request :post "/persons")
                        (body (json/generate-string {:id :4
                                                     :connections [:6 :5 :3]}))
                        (content-type "application/json")
                        (header "Accept" "application/json")))]
  (expect 201 (:status response))
  (expect #"/persons/4" (get-in response [:headers "Location"]))
  (expect [:3 :5 :6] (@influenza-social-graph :4)))

;; GET /persons/:id gets a person's connections
(let [response (app (request :get "/persons/1"))]
  (expect 200 (:status response))
  (expect {"id" "1"
           "connections" ["3" "2"]} (json/parse-string (:body response))))

;; POST /persons/:id/fraudulent tags a person as fraudulent
;; influence score should become 0
(let [response-tag-fraudulent (app (request :post "/persons/1/fraudulent"))]
  (expect 200 (:status response-tag-fraudulent))

  (let [response-new-ranking (app (request :get "/social-influence-ranking"))
        new-ranking (json/parse-string
                      (:body response-new-ranking))]
    (expect ["1" 0] (last new-ranking))))
