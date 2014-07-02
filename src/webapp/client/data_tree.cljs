(ns webapp.client.data-tree
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   [webapp.client.timers]
   )
  (:use
   [webapp.client.helper                    :only  [when-data-path-equals
                                                    when-data-value-changes]]
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))












(when-data-path-equals    [:submit
                             :status]     "ConfirmedSender"

 (fn [ui]
    (om/update! ui [:ui
                      :request
                        :from-email
                          :confirmed]  true)))







(when-data-path-equals   [:submit :status]     "ConfirmedReceiver"

 (fn [ui]
   (go
    (om/update! ui [:ui :request :to-email :confirmed]  true)
    )))










(when-data-path-equals    [:submit]     "Submitted"

 (fn [ui]
     (log "sent")
     ))






(when-data-value-changes  [:top-companies]

 (fn [ui]
   (om/update! ui [:ui :companies :values]  (get-in @data-state [:top-companies]))
   ))



(when-data-value-changes  [:latest-endorsements]

 (fn [ui]
   (om/update! ui [:ui :latest-endorsements :values]
               (get-in @data-state [:latest-endorsements]))
   ))




(when-data-value-changes  [:company-details]

 (fn [ui]
   (om/update! ui [:ui :company-details :skills]
               (get-in @data-state [:company-details]))
   ))
