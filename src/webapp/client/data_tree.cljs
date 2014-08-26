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
    [cljs.core.async.macros :refer [go]]))

(c/ns-coils 'webapp.client.data-tree)










(c/==data    [:submit :status]     "ConfirmedSender"
    (c/-->ui [:ui :request :from-email :confirmed]  true))







(c/==data   [:submit :status]   "ConfirmedReceiver"
   (go
    (c/-->ui [:ui :request :to-email :confirmed]  true)
    (c/-->ui [:ui :request :show-connection-confirmation]  true)
    ))










(c/==data    [:submit]     "Submitted"
     (c/log "sent"))






(c/watch-data  [:top-companies]

   (c/-->ui [:ui :companies :values]  (c/<--data [:top-companies])))




(c/watch-data  [:latest-endorsements]

   (c/-->ui[:ui :latest-endorsements :values]  (c/<--data [:latest-endorsements]))
   )






(c/watch-data  [:company-details]
   (c/-->ui [:ui :company-details :skills] (c/<--data [:company-details])))
