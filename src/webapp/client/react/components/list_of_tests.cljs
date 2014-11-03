(ns webapp.client.react.components.list-of-tests
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha])
  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j neo4j-1 sql-1 log
                                               watch-data  -->ui  <--data
                                               remote  defn-ui-component
                                               container  map-many  inline  text
                                               div  add-data-source  data
                                               component
                                               ]]))
(ns-coils 'webapp.client.react.components.list-of-tests)




(defn-ui-component  list-questions  [ui]
   (map-many
    #(container
      (inline "100%" (c/text "- " (:question  %1  ) )))
    (data  "read all questions for list" {
                                          :path       []
                                          :db-table   "learno_questions"
                                          :fields     "question"
                                          :ui-state   ui})))




(defn-ui-component      component-loads-of-stuff   [ui]
  (container
   (component  list-questions  ui  [:questions])

   (div {:style {:padding "20px"}})

   (map-many
    #(container
      (inline "250px" (text "*" (:name %1) ))
      (inline ""      (text (:questions_answered_count %1))))
    (data  "read all tests for list" {
                                      :path       [:tests]
                                      :db-table   "learno_tests"
                                      :fields     "name"
                                      :ui-state   ui}))

   (div {:style {:padding "20px"}})

   (map-many
    #(container
      (inline "450px" (text "*" (:keyword %1) )))
    (data  "read all keywords" {
                                      :path       [:keywords]
                                      :db-table   "learno_keywords"
                                      :fields     "keyword"
                                      :ui-state   ui}))

   ))
