(ns webapp.framework.client.main
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   [webapp.framework.client.protocols]
   [webapp.framework.client.records]
  )

  (:use
   [webapp.framework.client.coreclient      :only  [log remote-fn neo4j-fn]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-controls-state
                                                    reset-app-state
                                                    playbackmode
                                                    start-component
                                                    init-fn
                                                    data-state
                                                    app-watch-on?]]
   [webapp.framework.client.components.system-container :only  [main-view]]
   [webapp.framework.client.components.playback  :only  [playback-controls-view ]]
   )
  (:use-macros
   [webapp.framework.client.coreclient :only  [ns-coils remote neo4j]]
   )
  (:require-macros
   [cljs.core.async.macros :refer [go]]))






(js/React.initializeTouchEvents  true)
(def history-order (atom 0))
(def start-time (.getTime (js/Date.)))
(def session-id (atom ""))





(defn add-browser-details [field value]
  (reset! app-state (assoc-in @app-state [:system field ] value))
  )


(defn add-view-details [field value]
  (reset! app-state (assoc-in @app-state [:view field ] value))
  )




(defn detect-browser []
  (add-browser-details :cookie-enabled (.-cookieEnabled js/navigator))
  (add-browser-details :language (.-language js/navigator))
  (add-browser-details :platform (.-platform js/navigator))
  (add-browser-details :who-am-i (js/sayswho))

  (add-view-details :width js/viewportw)
  (add-view-details :height js/viewporth)
)




(defn main []
  (go
   (reset-app-state)
   (@init-fn)
   (detect-browser)
   (let [session (:value (remote !create-session
                                     {
                                      :init-state (with-out-str (prn @app-state))
                                      :browser    (str (-> @app-state :system :platform) ","
                                                       (-> @app-state :system :who-am-i))
                                      })  )]
     (log session)
     (reset! session-id session))





    (let [
          tx-chan       (chan)
          tx-pub-chan   (pub tx-chan (fn [_] :txs))
          ]
          (om/root   main-view
                     app-state

                 {:target (. js/document (getElementById "main"))
                  :shared {:tx-chan tx-pub-chan}
                  :tx-listen
                  (fn [tx-data root-cursor]
                    (if @app-watch-on?
                    (go
                     ;(log (str "txdata:::" tx-data))
                     ;(log (str "path:::" (:path tx-data)))
                     ;(log (str "new-value:::"
                     ;          (with-out-str (prn (:new-value tx-data)))))
                     (put! tx-chan [tx-data root-cursor])

                     (let [ts   (- (.getTime (js/Date.)) start-time)]
                     (remote !add-history
                                 {
                                  :session-id    @session-id
                                  :history-order (swap! history-order inc)
                                  :path          (:path tx-data)
                                  :new-value     (with-out-str (prn
                                                                (:new-value tx-data)))
                                  :timestamp     ts
                                  })
                     (neo4j "match (w:WebSession) where
                                w.session_id={session_id}
                                set w.time = {timestamp}
                                return w
                                "
                                {:session_id    @session-id
                                 :timestamp     ts}
                                "w")))))}))))




(defn get-web-sessions []
  (go
   (neo4j "match (n:WebSession) return
          n.session_id as session_id,
          n.time as time,
          n.browser as browser,
          n.start_time as start_time
          order by n.start_time desc"
          {}
          ["session_id" "start_time" "browser" "time"])))






(defn admin []
  (go
   (let [ll  (<! (get-web-sessions))]

     (reset! playbackmode true)

     (reset! playback-controls-state (assoc-in
                                      @playback-controls-state
                                      [:data :sessions-count]  (count ll)))
     (reset! playback-controls-state (assoc-in
                                      @playback-controls-state
                                      [:data :sessions]  (into [] ll)))




     (om/root
      playback-controls-view
      playback-controls-state
      {:target (js/document.getElementById "playback_controls")})
     )))















(defn debug []
  (go
   (let [ll  (<! (get-web-sessions))]

     (reset! playbackmode true)

     (reset! playback-controls-state (assoc-in
                                      @playback-controls-state
                                      [:data :sessions-count]  (count ll)))
     (reset! playback-controls-state (assoc-in
                                      @playback-controls-state
                                      [:data :sessions]  (into [] ll)))




     (om/root
      playback-controls-view
      playback-controls-state
      {:target (js/document.getElementById "playback_controls")})
     )))








(defn ^:export defaultmain [app owner]
  (dom/div nil
           (dom/h2 nil "Clojure on coils")
           ))

(defn ^:export defaultinit []
  nil
  )


;--------------------------------------------------------
(defn ^:export  load_main [setup-config]
;--------------------------------------------------------
  (reset!  start-component  (get setup-config :start-component))
  (reset!  init-fn          (get setup-config :setup-fn))
   (main))




;--------------------------------------------------------
(defn ^:export load_admin [setup-config]
;--------------------------------------------------------
  (reset!  start-component  (get setup-config :start-component))
  (reset!  init-fn          (get setup-config :setup-fn))
  (admin))






;--------------------------------------------------------
(defn ^:export load_debug [setup-config]
;--------------------------------------------------------
  (reset!  start-component  (get setup-config :start-component))
  (reset!  init-fn          (get setup-config :setup-fn))
  (debug))
