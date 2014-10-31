(ns webapp.client.react.components.list-of-tests
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j neo4j-1 sql-1 log
                                               watch-data
                                               -->ui
                                               <--data
                                               remote
                                               ]])
  (:use
   [webapp.framework.client.system-globals  :only  [app-state
                                                    reset-app-state
                                                    ui-watchers
                                                    data-watchers
                                                    data-state
                                                    add-init-state-fn
                                                    update-data
                                                    touch
                                                    ]]
   )
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(c/ns-coils 'webapp.client.react.components.list-of-tests)








(def data-sources (atom  {}))
@data-sources








(defn add-data-source [data-source-name
                       {
                        fields               :fields
                        }
                       ]
  (if (not (get @data-sources data-source-name))
    (do
      (reset! data-sources
              (assoc @data-sources data-source-name {}))

      (go
       (update-data [:tables :top-tests]
                    (remote !make-sql
                            {:fields "name, questions_answered_count"}) ))

      (watch-data [:tables :top-tests]
                  (do
                    (-->ui [:ui :tests :values] (<--data [:tables :top-tests]))
                    ))
      ) ))






(defn data [name-of-reader    {
                               path                 :path
                               ui-state             :ui-state
                               interval-in-millis   :interval-in-millis
                               fields               :fields
                               }]
  (add-data-source  name-of-reader
                    {
                       :fields    fields
                     })
  (get ui-state :values)
  )



;(get-top-tests)




(c/defn-ui-component     component-list-of-tests   [tests]

  (c/map-many

   #(c/container
     (c/inline "150px" (c/text (:name %1) ))
     (c/inline ""      (c/text (:questions_answered_count %1)))
     )

   (data  "read all tests for list"
          {
           :path       []
           :fields     "name, questions_answered_count"
           :ui-state   tests
           })))
