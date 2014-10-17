(ns webapp.client.timers
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [webapp.framework.client.coreclient   :as  c  :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.client.ui-helpers                :only  [validate-email ]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    reset-app-state
                                                    ui-watchers
                                                    data-watchers
                                                    data-state
                                                    add-init-state-fn
                                                    update-data
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))







(defn get-top-companies-from-database []

  (go
   (c/log "getting companies")
   (let [top-companies (c/remote "get-top-companies" {})]
     (if (c/ok top-companies)
       (do
         (update-data [:top-companies] top-companies)
         ;(set! (.-innerHTML (.getElementById js/document "playback_state")) (pr-str top-companies))
         )))))



(defn get-latest-endorsements-from-database []

  (go
   (c/log "getting latest endorsements")
   (let [latest-endorsements (c/remote "get-latest-endorsements" {})]
     (if (c/ok latest-endorsements)
       (do
         (update-data [:latest-endorsements] latest-endorsements)
         ;(set! (.-innerHTML (.getElementById js/document "playback_state")) (pr-str latest-endorsements))
         )))))





(add-init-state-fn  "get top companies"  get-top-companies-from-database)
(add-init-state-fn  "get latest endorsements"  get-latest-endorsements-from-database)


(def tt (atom 1))

(defn my-timer []
    (swap! tt inc)
    (c/log (str "Called timer: " @tt))
    (if






     (= (get-in @data-state [:remote :notifications :new-member]) "waiting")


     (go
       (let [res

              (c/remote    email-confirmed
               {
                :endorsement-id
                (get-in @data-state [:session  :endorsement-id])})
             ]
         (c/log (str "Checking sender " @tt " " res))
         (if (res :value)
           (do
             (update-data [:remote :notifications :new-member]  "EmailConfirmed"))))))






(if
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



