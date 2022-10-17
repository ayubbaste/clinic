(ns clinic.core
  (:require [ring.adapter.jetty   :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [compojure.core       :refer [defroutes GET POST PUT DELETE ANY]]
            [compojure.route      :as route]
            [clojure.pprint       :as pprint]
            [clinic.view          :refer [index-page
                                          patient-page
                                          add-new-patient
                                          delete-patient]]))


;; a place to save the object run-jetty returns
(defonce server (atom nil))


(defroutes app
  (GET    "/" request index-page)
  (POST   "/" request add-new-patient)
  (GET    "/:id" request patient-page)
  (DELETE "/:id" request delete-patient)
  (ANY    "/echo" req {:status 200
                       :headers {"Content-Type" "text/plain"}}
                       :body (with-out-str (pprint/pprint req)))
  (route/not-found {:status 404
                    :headers {"Content-Type" "text/plain"
                    :body "My Error 404 - Page not found"}})
  )

(def app (wrap-keyword-params (wrap-params app)))

;; function to start the server
(defn start-server []
  (reset! server
    (jetty/run-jetty (fn [req] (app req))  ;; call app
                     {:port 3001           ;; listen on port 3001
                      :join? false})))     ;; don't block the main thread


;; call the .stop method on the object stored in the atom
(defn stop-server []
  (when-some [s @server]  ;; check if there is an object in the atom
    (.stop s)             ;; call the .stop method
    (reset! server nil))) ;; overwrite the atom with nil
