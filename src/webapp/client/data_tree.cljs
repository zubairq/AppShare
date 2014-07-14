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
                                                    ]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    data-tree!
                                                    data-tree
                                                    update-ui
                                                    get-in-tree
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient :only  [when-data-path-equals
                                               when-data-value-changes
                                               ui-tree!
                                               ui-tree
                                               ns-coils
                                               ]])
  )

(ns-coils 'webapp.client.data-tree)










(when-data-path-equals    [:submit :status]     "ConfirmedSender"

    (ui-tree! [:ui :request :from-email :confirmed]  true))







(when-data-path-equals   [:submit :status]     "ConfirmedReceiver"

   (go
    (ui-tree! [:ui :request :to-email :confirmed]  true)
    ))










(when-data-path-equals    [:submit]     "Submitted"

     (log "sent")
     )






(when-data-value-changes  [:top-companies]

   (ui-tree! [:ui :companies :values]  (data-tree [:top-companies]))
   )




(when-data-value-changes  [:latest-endorsements]

   (ui-tree! [:ui :latest-endorsements :values]  (data-tree [:latest-endorsements]))
   )






(when-data-value-changes  [:company-details]
   (ui-tree! [:ui :company-details :skills] (data-tree [:company-details])))
