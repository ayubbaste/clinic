(ns clinic.view
  (:require [next.jdbc   :as jdbc]
            [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.form :refer [form-to
                                 label
                                 text-field
                                 email-field
                                 text-area
                                 radio-button
                                 hidden-field
                                 submit-button]]

  ))

(def db (jdbc/get-datasource {:dbtype "postgresql"
                              :dbname "clj_contacts"
                              :host "0.0.0.0"
                              :port "7777"
                              :user "postgres"
                              :password "postgres"}))

(def ds (jdbc/get-datasource db))


(defn index-page
  [request]

  (def patients (with-open [connection (jdbc/get-connection ds)]
                  (jdbc/execute! connection ["SELECT * FROM contacts;"])))

  (defn labeled-radio [label]
    [:label (radio-button {:ng-model "gender"} "gender" false label)
     (str label "    ")])

  (let [content (html5 [:head
        [:meta {:charset "UTF-8"}]
        [:title "My title"]
        (include-css "/css/bootstrap.min.css")
        (include-css "/css/main.css")
        ]
      [:body
        [:div {:class "content"}
          [:div {:class "container"}
            [:h3 "Patients"]
            [:table
              (for [patient patients]
                [:tr
                  [:td (get patient :contacts/id)]
                  [:td [:a {:href (:to-uri "/" (get patient :contacts/id))} (get patient :contacts/first_name) (get patient :contacts/last_name)]]
                ])]

            [:br]
            [:br]

            [:h4 "Add new patient"]

            (form-to [:post "/"]
              [:p {:class "ddfsdfds"}
               (text-field {:class "dfsdfs" :ng-model "Name" :placeholder "Name"} "name")]

              [:p {:class "ddfsdfds"}
               (text-field {:class "dfsdfs" :ng-model "LastName" :placeholder "Last name"} "last-name")]

              [:p {:class "ddfsdfds"}
               (email-field {:class "dfsdfs" :ng-model "Email" :placeholder "Email"} "email")]

              [:div {:class "form-group"}
               (reduce conj [:div {:class "btn-group"}] (map labeled-radio ["male" "female"]))]

              [:p {:class "ddfsdfds"}
               (text-area {:class "dfsdfs" :cols 50 :rows 2 :ng-model "Text-Area" :placeholder "Notes"} "notes")]
              [:br]
              (submit-button "Add new patient"))
            ]
          ]
        ]
       )]

          (if content
            {:status 200
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body content}
            {:status 404
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body {:error "Not found"}})))


(defn add-new-patient
  [request]

  (def first-name (get-in request [:form-params "name"]))
  (def last-name (get-in request [:form-params "last-name"]))
  (def email (get-in request [:form-params "email"]))
  (def gender (get-in request [:form-params "gender"]))
  (def notes (get-in request [:form-params "notes"]))
  (with-open [connection (jdbc/get-connection ds)]
    (jdbc/execute! connection ["INSERT INTO contacts (first_name, last_name, email) VALUES (?, ?, ?) RETURNING id;" first-name, last-name, email]))

    ;;{:status 201
     ;;:body (get-contact-by-id created-id)}))

  (let [content (html5 [:head
          [:title "New patient added successfuly"]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/main.css")
          [:meta {:charset "UTF-8"}]]
         [:body
           [:div {:class "content"}
             [:div {:class "container"}
               [:p [:i "Breadcrumbs: " [:a {:href "/"} "Home"]]]
               [:h3 "New patient added successfuly"]
               [:p "Name: "      first-name]
               [:p "Last name: " last-name]
               [:p "Email: "     email]
               [:p "Gender: "    gender]
               [:p "Notes: "     notes]
               [:br]
               [:br]
             ]
           ]
         ]
        )]

          (if content
            {:status 201
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             ;;:body (slurp (get-in request [:params "name"]))}
             :body content}
            {:status 404
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body {:error "Not found"}})))


(defn patient-page
  [request]

  (def id (Integer. (get-in request [:params :id])))

  (def patient (with-open [connection (jdbc/get-connection ds)]
                 (jdbc/execute! connection ["SELECT * FROM contacts WHERE id=?;" id])))

  (let [content (html5 [:head
          [:title "My title"]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/main.css")
          [:meta {:charset "UTF-8"}]]
         [:body
           [:div {:class "content"}
             [:div {:class "container"}
               [:p [:i "Breadcrumbs: " [:a {:href "/"} "Home"]]]
               [:h3 (:contacts/first_name (first patient))
                    (:contacts/last_name (first patient))]
               [:p [:em "Patient id"] ": " (:contacts/id (first patient))]
               [:p [:em "Name"] ": "       (:contacts/first_name (first patient))]
               [:p [:em"Last name"] ": "   (:contacts/last_name (first patient))]
               [:p [:em "Email"] ": "      (:contacts/email (first patient))]
               [:br]
               (form-to [:delete "/" (:contacts/id (first patient))]
                ;;(hidden-field (:contacts/id (first patient)))
                (submit-button "Delete patient"))
             ]
           ]
         ]
        )]

          (if content
            {:status 200
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body content}
            {:status 404
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body {:error "Not found"}})))


(defn delete-patient
  [request]

  (println request))

;;  (def id (get-in request [:form-params :id]))
;;
;;  (with-open [connection (jdbc/get-connection ds)]
;;    (jdbc/execute! connection ["DELETE FROM contacts WHERE id=?;" id]))
;;
;;   {:status 200
;;    :body {;;:deleted true
;;           :body "OOOOOOOOOOOOOOOKKKKKK !"}})
;;           ;;:body (slurp (get-in request [:params "id"]))}})


;;  (let [content (html5 [:head
;;          [:title "My title"]
;;          (include-css "/static/css/main.css")
;;          [:meta {:charset "UTF-8"}]]
;;         [:body
;;          [:p [:i "Breadcrumbs: " [:a {:href "/"} "Home"]]]
;;          [:h3 "Patient's card id: deleted"]
;;         ])]
;;
;;    (if content
;;      {:status 200
;;       :body {:deleted true
;;              :body content}}
;;      {:status 404
;;       :body {:deleted false
;;              :error "Unable to delete contact"}})))
;;
;;  (let [deleted-count (with-open [connection (jdbc/get-connection ds)]
;;                        (jdbc/execute! connection ["DELETE FROM contacts WHERE id=?;" id]))]

;;    (if (= 1 deleted-count)
;;      {:status 200
;;       :body {:deleted true
;;              :body content}}
;;      {:status 404
;;       :body {:deleted false
;;              :error "Unable to delete contact"}})))
