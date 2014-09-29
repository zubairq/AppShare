(ns webapp.client.data-tree
  (:require
   [goog.net.cookies                     :as cookie]
   [om.core                              :as om    :include-macros true]
   [om.dom                               :as dom   :include-macros true]
   [webapp.framework.client.coreclient   :as c     :include-macros true]
   [cljs.core.async                      :refer   [put! chan <! pub timeout]]
   [om-sync.core                         :as async]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   [ankha.core                           :as ankha]
   [webapp.client.timers]
   )

  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils
                                               sql
                                               log
                                               neo4j
                                               defn-ui-component
                                               a
                                               div
                                               write-ui
                                               ==data
                                               -->ui
                                               <--ui
                                               -->data
                                               <--data
                                               watch-data
                                               ]]
   )
  )

(ns-coils 'webapp.client.data-tree)










(==data    [:submit :status]     "ConfirmedSender"
    (-->ui [:ui :request :from-email :confirmed]  true))







(==data   [:submit :status]   "ConfirmedReceiver"
   (go
    (-->ui [:ui :request :to-email :confirmed]  true)
    (-->ui [:ui :request :show-connection-confirmation]  true)
    ))










(==data    [:submit]     "Submitted"
     (log "sent"))






(watch-data  [:top-companies]

   (-->ui [:ui :companies :values]  (<--data [:top-companies])))




(watch-data  [:latest-endorsements]

   (-->ui[:ui :latest-endorsements :values]  (<--data [:latest-endorsements]))
   )






(watch-data  [:company-details]
   (-->ui [:ui :company-details :skills] (<--data [:company-details])))
