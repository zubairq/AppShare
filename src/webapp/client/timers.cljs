(ns webapp.client.timers
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
   [webapp.client.ui-helpers                :only  [validate-email validate-full-name  validate-endorsement]]
   [webapp.client.helper                    :only  [when-path-equals when-value-changes]]
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    add-init-state-fn
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))







(defn get-top-companies-from-database []

  (go
   (log "getting companies")
   (let [top-companies (<! (remote "get-top-companies" {}))]

     (update-data [:top-companies] top-companies)
     )
   ))



(defn get-latest-endorsements-from-database []

  (go
   (log "getting latest endorsements")
   (let [latest-endorsements (<! (remote "get-latest-endorsements" {}))]

     (update-data [:latest-endorsements] latest-endorsements)
     )
   ))





(add-init-state-fn  "get top companies"  get-top-companies-from-database)
(add-init-state-fn  "get latest endorsements"  get-latest-endorsements-from-database)


(def tt (atom 1))

(defn my-timer []
  (go
    (swap! tt inc)
    (log (str "Called timer: " @tt))
    (cond





     (and
      (= (get-in @data-state [:submit :status])  "Submitted")
      (get-in @data-state [:submit :request :endorsement-id]))

     (do
       (let [res
             (<!
              (remote
               "sender-confirmed"
               {
                :endorsement-id
                (get-in @data-state [:submit :request :endorsement-id])}))
             ]
         (log (str "Checking sender " @tt " " res))
         (if (res :value)
           (do
             (update-data [:submit :status]  "ConfirmedSender")))))




     (and
      (= (get-in @data-state [:submit :status])  "ConfirmedSender")
      (get-in @data-state [:submit :request :endorsement-id]))

     (do
        (let [res (<! (remote "receiver-confirmed" {
                :endorsement-id (get-in @data-state
                                        [:submit :request :endorsement-id])}))
              ]
         (log (str "Checking receiver " @tt " " res))
          (if (res :value)
            (do
              (update-data [:submit :status]  "ConfirmedReceiver")))))





     (and
      (= (get-in @app-state [:ui :tab])  "browser")
      (= (-> @app-state :ui :tab-browser) "top companies")
     )

     (get-top-companies-from-database)




     (and
      (= (get-in @app-state [:ui :tab])  "browser")
      (= (-> @app-state :ui :tab-browser) "latest endorsements")
     )

     (get-latest-endorsements-from-database)


 )))


(add-init-state-fn "timer function" #(js/setInterval my-timer 15000))



