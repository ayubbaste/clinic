(ns clinic.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core     :refer [defroutes GET POST ANY]]
            [compojure.route    :as route]
            [clojure.pprint     :as pprint]
            [clinic.view        :refer [index-page patient-page]]))


;; a place to save the object run-jetty returns
(defonce server (atom nil))


(defroutes app
  (GET "/" params index-page)
  (POST "/" params (str (:wrap-multipart-params params)))
  (GET "/:id" params patient-page)
  (ANY "/echo" req {:status 200
                    :headers {"Content-Type" "text/plain"}}
                    :body (with-out-str (pprint/pprint req)))
  (route/not-found {:status 404
                    :headers {"Content-Type" "text/plain"
                    :body "My Error 404 - Page not found"}})
  )


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
