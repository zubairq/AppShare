(ns webapp.client.links
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
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





(defn  ^:export confirmLink [uuid-code]
  (go
   (let [ l (<! (remote "confirm-sender-code"
                        {
                         :sender-code   uuid-code
                         }))]
     (cond
      (:error l)
      (let [ l (<! (remote "confirm-receiver-code"
                           {
                            :receiver-code   uuid-code
                            }))]
        (cond
         (:error l)
         (.write js/document (:error l))

         :else
         (.write js/document (str "Your email address has been confirmed"))
         )
        )

      :else
      (.write js/document (str "Your email address has been confirmed"))
      )
     )
   )
  []
  )
