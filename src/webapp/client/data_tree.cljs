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
   [webapp.framework.client.coreclient      :only  [log
                                                    remote
                                                    when-data-path-equals-fn
                                                    when-data-value-changes-fn
                                                    update-ui
                                                    ]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    remove-debug-event
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    <--data
                                                    -->data

                                                    get-in-tree
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient :only  [==data
                                               watch-data
                                               <--ui
                                               -->ui
                                               ns-coils
                                               ]])
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
     (log "sent")
     )






(watch-data  [:top-companies]

   (-->ui [:ui :companies :values]  (<--data [:top-companies]))
   )




(watch-data  [:latest-endorsements]

   (-->ui[:ui :latest-endorsements :values]  (<--data [:latest-endorsements]))
   )






(watch-data  [:company-details]
   (-->ui [:ui :company-details :skills] (<--data [:company-details])))
