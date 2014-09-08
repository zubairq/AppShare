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
   [webapp.client.ui-helpers                :only  [validate-email ]]
   [webapp.framework.client.coreclient      :only  [log
                                                    remote
                                                    remove-debug-event
                                                    ok
                                                    ]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    reset-app-state
                                                    ui-watchers
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
     (if (ok top-companies)
       (do
         (update-data [:top-companies] top-companies)
         ;(set! (.-innerHTML (.getElementById js/document "playback_state")) (pr-str top-companies))
         )))))



(defn get-latest-endorsements-from-database []

  (go
   (log "getting latest endorsements")
   (let [latest-endorsements (<! (remote "get-latest-endorsements" {}))]
     (if (ok latest-endorsements)
       (do
         (update-data [:latest-endorsements] latest-endorsements)
         ;(set! (.-innerHTML (.getElementById js/document "playback_state")) (pr-str latest-endorsements))
         )))))





(add-init-state-fn  "get top companies"  get-top-companies-from-database)
(add-init-state-fn  "get latest endorsements"  get-latest-endorsements-from-database)


(def tt (atom 1))

(defn my-timer []
    (swap! tt inc)
    (log (str "Called timer: " @tt))
    (cond





     (and
      (= (get-in @data-state [:submit :status])  "Submitted")
      (get-in @data-state [:submit :request :endorsement-id]))

     (go
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

     (go
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


 ))


(add-init-state-fn "timer function" #(js/setInterval my-timer 15000))



