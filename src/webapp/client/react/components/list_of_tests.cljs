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
    (data  "the questions" {
                               :db-table "learno_questions"
                               :path       []
                                :fields     "id,question"
                                :ui-state   ui})))








(defn-ui-component      component-loads-of-stuff   [ui]
  (container
   (component  list-questions  ui  [:questions])

   (div {:style {:padding "20px"}})

   (map-many
    #(container
      (inline "330px" (text "*" (:name %1) ))
      (inline ""      (text (:questions_answered_count %1))))
    (data  "the tests" {
                        :db-table "learno_tests"
                        :path       [:tests]
                           :fields     "id,name"
                           :ui-state   ui}))

   (div {:style {:padding "20px"}})


   (map-many
    #(container
      (inline "450px" (text "*" (:keyword %1) )))

    (data  "the keywords" {
                           :db-table   "learno_keywords"
                           :path       [:keywords]
                           :fields     "id,keyword"
                           :ui-state   ui}))

   ))
