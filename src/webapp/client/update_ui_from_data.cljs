(ns webapp.client.update-ui-from-data
  (:require
   [goog.net.cookies                     :as cookie]
   [om.core                              :as om    :include-macros true]
   [om.dom                               :as dom   :include-macros true]
   [webapp.framework.client.coreclient   :as c     :include-macros true]
   [cljs.core.async                      :refer [put! chan <! pub timeout]]
   [om-sync.core                         :as async]
   [clojure.data                         :as data]
   [webapp.client.timers]
   [clojure.string                       :as string]
   [ankha.core                           :as ankha]
   )

  (:use
   [webapp.framework.client.components.main                    :only   [main-view]]
   [webapp.framework.client.system-globals                     :only   [app-state  data-state  set-ab-tests]]
   [webapp.client.react.views.learno                           :only   [main-learno-view]]
   )

  (:require-macros
   [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j neo4j-1 sql-1 log
                                               watch-data
                                               -->ui
                                               <--data]]
   )
  )
(c/ns-coils 'webapp.client.update-ui-from-data)



