(ns webapp.client.init
  (:require
   [goog.net.cookies                     :as cookie]
   [om.core                              :as om    :include-macros true]
   [om.dom                               :as dom   :include-macros true]
   [webapp.framework.client.coreclient   :as c     :include-macros true]
   [cljs.core.async                      :refer [put! chan <! pub timeout]]
   [om-sync.core                         :as async]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   [ankha.core                           :as ankha]
   )

  (:use
   [webapp.framework.client.components.main                    :only   [main-view]]
   [webapp.framework.client.system-globals                     :only   [app-state  data-state  set-ab-tests]]
   [webapp.client.views.learno                                 :only   [main-learno-view]]
   )

  (:require-macros
   [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j neo4j-1 sql-1 log]]
   )
  )
(c/ns-coils 'webapp.client.init)





(def  ^:export setup
  {
   :start-component
   main-learno-view

   :setup-fn
   (fn[]
     (do
     (reset!
      app-state

      (assoc-in
       @app-state [:ui]
       {


        }))


     (reset! data-state {
                         :submit {}
                         })


     (set-ab-tests {
                    })
  ))})





(go (log  (sql-1 "select count(*) from learno_tests " {})))
