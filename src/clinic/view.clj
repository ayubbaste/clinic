(ns clinic.view
  (:require [next.jdbc   :as jdbc]
            [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.form :refer [form-to
                                 label
                                 text-field
                                 email-field
                                 text-area
                                 radio-button
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
    [:label (radio-button {:ng-model "user.gender"} "user.gender" false label)
     (str label "    ")])

  (let [content (html5 [:head
        [:title "My title"]
        (include-css "css/main.css")
        [:meta {:charset "UTF-8"}]]
      [:body
        [:h1 "Patients"]
        [:p "(SQL response objects)"]
        [:table
          [:th "Id"]
          [:th "Name"]
          [:th "Last name"]
          [:th "Email"]
          (for [patient patients]
            [:tr
              [:td (get patient :contacts/id)]
              [:td [:a {:href (:to-uri "/" (get patient :contacts/id))} (get patient :contacts/first_name)]]
              [:td (get patient :contacts/last_name)]
              [:td (get patient :contacts/email)]
            ])]

        [:br]
        [:br]

        [:h2 "Add new patient"]

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
        ])]

          (if content
            {:status 200
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body content}
            {:status 404
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body {:error "Not found"}})))


(defn patient-page
  [request]

  (def patient (with-open [connection (jdbc/get-connection ds)]
                 (jdbc/execute! connection ["SELECT * FROM contacts WHERE id=?;" (Integer. (get-in request [:params :id]))])))

  (let [content (html5 [:head
          [:title "My title"]
          (include-css "css/main.css")
          [:meta {:charset "UTF-8"}]]
         [:body
          [:p [:a {:href "/"} "Home"]]
          [:h1 "Patient id "  (:contacts/id (first patient))]
          [:p "Name: "        (:contacts/first_name (first patient))]
          [:p "Last name: "   (:contacts/last_name (first patient))]
          [:p "Email: "       (:contacts/email (first patient))]
        ])]

          (if content
            {:status 200
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body content}
            {:status 404
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body {:error "Not found"}})))
