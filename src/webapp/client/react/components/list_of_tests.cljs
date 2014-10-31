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






(defn get-top-tests []
  (go
   (update-data [:tables :top-tests]
    (sql
     "select
         name,
         questions_answered_count
     from
     learno_tests
     where
     questions_answered_count is not null
     order by
     questions_answered_count desc
     limit 10" ) ))
  )




(watch-data [:tables :top-tests]
            (do
              (-->ui [:ui :tests :values] (<--data [:tables :top-tests]))
             ))


(defn data [name-of-reader    {
                               path    :path
                               tests   :ui-state
                               }]
  (get tests :values)
  )

;(get-top-tests)
(add-init-state-fn  "timer function"  #(js/setInterval  get-top-tests  1000))

(c/defn-ui-component     component-list-of-tests   [tests]

  (c/map-many

   #(c/container
     (c/inline "150px" (c/text (:name %1) ))
     (c/inline ""      (c/text (:questions_answered_count %1)))
     )

   (data  "read all tests for list"
          {
           :path       []
           :ui-state   tests
           })))
