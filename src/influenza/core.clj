(ns influenza.core
  (:gen-class)
  (:use compojure.core
        ring.middleware.json)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [clojure.java.io :as io]
            [influenza.social-graph :refer :all]))

(def ^:private file-data
  (-> (io/resource "edges")
      (io/file)
      (slurp)))

(def influenza-social-graph (atom (load-social-graph file-data)))
(def influenza-frauds (atom []))

(defroutes app-routes
  (GET "/social-influence-ranking" []
       (response (rank-influence @influenza-social-graph
                                 @influenza-frauds)))
  (GET "/persons/:id" [id] (response {:id id
                                      :connections (@influenza-social-graph (keyword id))}))
  (POST "/persons" request
        (let [person (get-in request [:body])]
          (doall (map
                   #(swap!
                     influenza-social-graph
                     add-connection [(keyword (:id person))
                                     (keyword %)])
                     (:connections person)))
          {:status 201
           :headers {"Location" (str "/persons/" (:id person))}}))
  (POST "/persons/:id/fraudulent" [id]
        (swap! influenza-frauds conj (keyword id))
        {:status 200})
  (GET "/" []
       (response {:message "welcome to influenza! check the README.md for more info"}))
  (route/resources "/")
  (route/not-found (response {:message "resource not found :("})))

(defn wrap-log-request [handler]
  (fn [req] (println req) (handler req)))

(def app
  (-> app-routes
      wrap-log-request
      (wrap-json-response {:pretty true})
      (wrap-json-body {:keywords? true})))
