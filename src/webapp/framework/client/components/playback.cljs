(ns webapp.framework.client.components.playback
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
   [webapp.framework.client.coreclient     :only  [log remote-fn neo4j-fn]]
   [webapp.framework.client.system-globals :only  [app-state   playback-app-state
                                                   playback-controls-state
                                                   reset-app-state
                                                   reset-playback-app-state]]
   [webapp.framework.client.components.system-container :only  [main-view]]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [remote neo4j]]
   )
  (:require-macros
   [cljs.core.async.macros :refer [go]]))





(def page-length 3)

(def playbacktime (atom 0))

(defn playback-session [& {:keys
                           [session-id]}]
  (go
   (reset! playback-controls-state
           (update-in @playback-controls-state
                      [:ui :current-session]
                      (fn[_] session-id)))
   (let [
            init-state  (neo4j "match (n:WebSession) where
                                   n.session_id={session_id}
                                   return n.init_state"
                                   {:session_id    session-id})


            ll          (neo4j "
                                   match (r:WebRecord) where
                                   r.session_id={session_id}
                                   return r order by r.seq_ord
                                   "
                                   {:session_id    session-id}
                                   "r")]

        (om/root
         main-view
         playback-app-state
         {:target (js/document.getElementById "playback_canvas")})


        (reset! playback-app-state (cljs.reader/read-string init-state))
        ;(log (str "sssss=" init-state))
        (doseq [item ll]
          (let [
                path      (cljs.reader/read-string (:path (into {} item )))
                content   (cljs.reader/read-string (:new_value (into {} item )))
                timestamp   (:timestamp (into {} item ))
                ]
            ;(log path)
            ;(log content)
            ;(log timestamp )
            (<! (timeout (-  timestamp @playbacktime)))
            (reset! playbacktime timestamp)


            (reset! playback-app-state
                    (update-in @playback-app-state
                               path
                               (fn[_] content)))

            nil
            )
          )
        (js/alert "session finished")
        ))
  )






(defn replay-session [session-id]
  (go
   (let [
         ll          (neo4j "match (n:WebSession) return n.session_id"
                        {} "n.session_id")
        ]

     (reset! playback-controls-state (assoc-in
                                      @playback-controls-state
                                      [:data :sessions]  (into [](take 5 ll))))

     (om/root
      main-view
      playback-app-state
      {:target (js/document.getElementById "playback_canvas")}))))









(defn playback-session-button-component [{:keys [ui
                                                 data
                                                 sessions
                                                 current-session]} owner]
  (reify

    om/IRenderState
    ;---------

    (render-state
     [this {:keys [highlight unhighlight]}]
      (let [
              session-id   (get-in data ["session_id"])
              browser      (get-in data ["browser"])
              start-time   (js/Date. (get-in data ["start_time"]))
              full-time    (/ (get-in data ["time"]) 1000)
              active       (= session-id current-session)
            ]
       (dom/div nil

              (dom/div
               #js {
                    :style      #js {:padding-top "40px"
                                     :backgroundColor
                                     (if
                                       (= (get-in ui
                                               [:sessions session-id :highlighted])
                                          "true")
                                       "darkgray"
                                       (if active
                                         "lightgray"
                                         "white")
                                       )
                                     }

                    :onClick
                      (fn [e]  (playback-session  :session-id  session-id))

                    :onMouseEnter
                      (fn[e]   (put! highlight    session-id))
                    :onTouchStart
                      (fn[e]   (put! highlight    session-id))

                    :onMouseLeave
                      (fn[e]   (put! unhighlight  session-id))
                    :onTouchEnd
                      (fn[e]   (put! highlight  session-id))
                    :onTouchMove
                      (fn[e]   (put! highlight  session-id))
                    }
               (str browser "  :::  " start-time  ", "
                    full-time " secs")))))))










(defn handle-change [app e]
  (om/update! app [:value] (.. e -target -value))
  )






;-----------------------------------------------------------------------------------------------
(defn labelled-field-component [{:keys [field]} owner]
  (reify

    ;---------
    om/IRender
    (render
    ;------
     [this]
     ;-----

     (dom/div #js {:className "input-group"}

              (dom/span
               #js {:className "input-group-addon"}
               (get field :label ""))
              (dom/input
               #js {:type        "text"
                    :className   "form-control"
                    :placeholder (get field :placeholder "")
                    :value       (get field :value)
                    :onChange    #(handle-change field %)
                    }))
      ))

)









(defn playback-controls-view [app owner]

  (reify

    om/IInitState
    ;------------

    (init-state [_]

                {
                   :highlight                (chan)
                   :unhighlight              (chan)
                   :show-ankha1              (chan)
                   :clear-replay-sessions    (chan)
                   :show-ankha               (chan)
                   :next-page                (chan)
                   :previous-page            (chan)
                })

    om/IWillMount
    ;------------
    (will-mount [_]
                (let [
                      highlight               (om/get-state owner :highlight)
                      unhighlight             (om/get-state owner :unhighlight)
                      clear-replay-sessions   (om/get-state owner :clear-replay-sessions)
                      show-ankha              (om/get-state owner :show-ankha)
                      show-ankha1             (om/get-state owner :show-ankha1)
                      next-page               (om/get-state owner :next-page)
                      previous-page           (om/get-state owner :previous-page)
                      ]
                  (go (loop []
                        (let [session (<! highlight)]
                          ;(log "****HIGHLIGHT")
                          (om/transact!
                           app
                           [:ui :sessions session :highlighted] (fn[x] "true" ))
                          (recur))))

                  (go (loop []
                        (let [session (<! unhighlight)]
                          ;(log "****UNHIGHLIGHT")
                          (om/transact!
                           app
                           [:ui :sessions session :highlighted] (fn[x] "false" ))
                          (recur))))
                  (go (loop []
                        (let [session (<! clear-replay-sessions)]
                          (log "****CLEAR REPLAY")

                          (let [ret (remote "!clear-playback-sessions"
                                      {:password (-> @app :ui :delete-password :value)
                                       })]
                            (if (ret :success)
                              (om/transact!
                               app
                               [:data :sessions] (fn[x] {} ))))
                          (recur))))

                  (go (loop []
                        (let [session (<! show-ankha)]
                          (log "****SHOW ANKHA")
                          (om/root
                            ankha/inspector
                            playback-app-state
                            {:target (js/document.getElementById "playback_state")})

                          (recur))))
                  (go (loop []
                        (let [session (<! show-ankha1)]
                          (log "****SHOW ANKHA1")
                          (om/root
                            ankha/inspector
                            playback-controls-state
                            {:target (js/document.getElementById "playback_ankha")})

                          (recur))))

                  (go (loop []
                        (let [session (<! next-page)]
                          (log "****NEXT PAGE")
                          (om/transact! app [:ui :current-page] (fn[x] (+ x 1)))

                          (recur))))

                  (go (loop []
                        (let [session (<! previous-page)]
                          (log "****PREVIOUS PAGE")
                          (om/transact! app [:ui :current-page] (fn[x] (- x 1)))

                          (recur))))



                  ))

    om/IRenderState
    ;--------------

    (render-state
     [this {:keys [highlight
                   unhighlight
                   clear-replay-sessions
                   show-ankha
                   show-ankha1
                   next-page
                   previous-page]}]
     (let [
           dfg           (- (* (+ (-> app :ui :current-page) 1) page-length) 1)
           session-count (-> app :data :sessions-count)
           ]

     (dom/div nil
              (dom/h2 nil "Playback web sessions")
              (dom/div nil (str (* (-> app :ui :current-page) page-length) "-"
                                  (if (< dfg (-> app :data :sessions-count))
                                    dfg
                                    (-> app :data :sessions-count)
                                 )
                                " of "
                                session-count))
              (if (> dfg page-length)
                (dom/button #js {:onClick (fn [e]
                                            (put! previous-page
                                                  true)
                                            )} "previous"))
              (if (< dfg session-count)
                (dom/button #js {:onClick (fn [e]
                                            (put! next-page
                                                  true)
                                            )} "Next"))

              (apply dom/ul nil
                     (om/build-all  playback-session-button-component
                                    (mapv
                                     (fn [x]
                                       {
                                        :ui         (-> app :ui)
                                        :sessions   (-> app :data :sessions)
                                        :current-session   (-> app :ui :current-session)
                                        :data       x
                                        }
                                       )
                                     (take page-length
                                           (drop (* page-length (-> app :ui :current-page))
                                           (-> app :data :sessions)))

                                     )
                                    {:init-state {:highlight    highlight
                                                  :unhighlight  unhighlight}}
                                    )
                     )

              (dom/button #js {:onClick (fn [e] (put! clear-replay-sessions
                                                      true))} "Delete")
              (om/build labelled-field-component
                        {:field (-> app :ui :delete-password)})


              (dom/button #js {:onClick (fn [e] (put! show-ankha
                                                      true))} "Show App state")
              (dom/button #js {:onClick (fn [e] (put! show-ankha1
                                                      true))} "Show Playback controls state")


              )))))
