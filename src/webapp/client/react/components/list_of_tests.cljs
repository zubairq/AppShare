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
                                               div  data-view
                                               component
                                               ]]))
(ns-coils 'webapp.client.react.components.list-of-tests)




(defn-ui-component  list-questions  [ui]
   (map-many
    #(container
      (inline "100%" (c/text "- " (-> %1 :value :question  ) )))
    (data-view  "the questions" {
                               :data-source  :questions
                               :path       []
                                :fields     [:id :question]
                                :ui-state   ui})))








(defn-ui-component      component-loads-of-stuff   [ui]
  (container
   (component  list-questions  ui  [:questions])

   (div {:style {:padding "20px"}})

   (map-many
    #(container
      (inline "330px" (text "*" (-> %1 :value :name) ))
      (inline ""      (text (:questions_answered_count %1))))
    (data-view  "the tests" {
                        :data-source  :tests
                        :path       [:tests]
                           :fields     [:id :name]
                           :ui-state   ui}))

   (div {:style {:padding "20px"}})


   (map-many
    #(container
      (inline "450px" (text "*" (-> %1 :value :keyword) )))

    (data-view  "the keywords" {
                           :data-source  :keywords
                           :path       [:keywords]
                           :fields     [:id :keyword]
                           :ui-state   ui}))

   ))
