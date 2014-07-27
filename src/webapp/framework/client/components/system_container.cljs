(ns webapp.framework.client.components.system-container
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha])

  (:use
   [webapp.framework.client.coreclient           :only  [log remote debug-mode component-fn]]
   [webapp.framework.client.system-globals       :only  [app-state
                                                         playback-app-state
                                                         playback-controls-state
                                                         playbackmode
                                                         ui-watchers
                                                         data-watchers
                                                         start-component
                                                         data-state
                                                         ab-tests
                                                         init-state-fns
                                                         data-and-ui-events-on?
                                                         add-debug-event
                                                         remove-debug-event
                                                         current-gui-path
                                                         ]]
   )
  (:use-macros
   [webapp.framework.client.neo4j      :only  [neo4j]]
   [webapp.framework.client.coreclient :only  [defn-ui-component ns-coils div component]]
   )
  (:require-macros
   [cljs.core.async.macros :refer [go]])

  )



(defn subtree-different? [orig-val new-val path]
  (let [
        orig-subset    (get-in orig-val  path)
        new-subset     (get-in new-val   path)
        ]
      (not (identical?  orig-subset  new-subset))))



(defn on-mouse [e app]

  (let [mousex (.-clientX e)
        mousey (.-clientY e)
        ]
    ;(log (str "x=" mousex ", " "y=" mousey  ))
    (if (or
         (< mousex (- (get-in @app [:pointer :mouse-x]) 30))
         (> mousex (+ (get-in @app [:pointer :mouse-x]) 30))
         (< mousey (- (get-in @app [:pointer :mouse-y]) 30))
         (> mousey (+ (get-in @app [:pointer :mouse-y]) 30))
         )

      (do
        (if (not (= (get-in @app [:pointer :mouse-x]) mousex))
          (om/update! app [:pointer :mouse-x] mousex))
        (if (not (= (get-in @app [:pointer :mouse-y]) mousey))
          (om/update! app [:pointer :mouse-y] mousey))
        ))))




(defn add-as-watch [the-ref  tree-name  watchers   args]

  (add-watch the-ref :events-change

             (fn [_ _ old-val new-val]

               (if @data-and-ui-events-on?
                 (doall

                  ;(. js/console log (pr-str "Events changed" new-val))
                  (for [watch @watchers]
                    (if (subtree-different? old-val new-val (:path watch))
                      (do
                        (log (str "Subtree changed: " (:path watch)))
                        (cond

                         (= (:type watch) "path equals")
                         ;------------------------------
                         (if (= (get-in new-val (:path watch)) (:value watch) )
                           (let [debug-id
                                 (add-debug-event
                                  :event-type  "event"
                                  :event-name  (str "==" tree-name " " (:path watch) " " (:value watch))
                                  )]
                             (apply (:fn watch) args)
                             (remove-debug-event  debug-id)

                             ))



                         (= (:type watch) "value change")
                         ;-------------------------------
                         (let [debug-id
                               (add-debug-event
                                :event-type  "event"
                                :event-name  (str "watch-" tree-name " " (:path watch))
                                )]
                           ;(js/alert (str "watch-" tree-name " " (:path watch)))
                           (apply (:fn watch) args)
                           (remove-debug-event  debug-id)

                           )



                         (= (:type watch) "record property equals")
                         ;-----------------------------------------
                         (let [records (filter
                                        (fn [r] (=  (get r (:field watch)) (:value watch)))
                                        (get-in new-val (:path watch))
                                        )]
                           (if (pos? (count records))
                             (apply (:fn watch) (conj args records))))



                         ))


                      :else
                      nil )))))))





(defn main-view [app owner]
  (reify
    ;---------------------------------------------------------
    om/IWillMount
    (will-mount [_]
                (do
                  ; set up the initial state
                  (dorun (for [init-state-fn  @init-state-fns]
                           (do
                             (init-state-fn)
                             )))

                  ; set up the AB tests
                  (log (str "AB TESTS: " (keys @ab-tests)))
                  (log (str "        : " @ab-tests))
                  (log (str "        : " (keys @ab-tests)))
                  (log (str "---"(get @ab-tests "graph type")))


                  (dorun (for [item  (keys @ab-tests)]
                           (do
                             (log (str "   ." (get @ab-tests item)))
                             (let [ ab-test  (get @ab-tests item) ]
                               (log (str "AB TEST: " ab-test))
                               (om/transact!
                                app
                                (:path ab-test)
                                #(str (:name (rand-nth (:choices ab-test) ) )))))))



                  ; set up the UI and data watchers
                  (go
                   (add-as-watch   app-state
                                   "ui"
                                   ui-watchers
                                   [app])


                   (add-as-watch   data-state
                                   "data"
                                   data-watchers
                                   [app])


                   )))

    ;---------------------------------------------------------
    om/IRenderState
    (render-state
     [this state]

     (dom/div nil
     (if @playbackmode
       (dom/div #js {:style #js {:font-weight "bold"}}
                (str (-> app :system :platform) ","
                     (-> app :system :who-am-i))))
     (dom/div #js {:style
                   (if @playbackmode #js {
                                          :position "relative"
                                          :border "2px black solid"
                                          :margin "10px"
                                          :width    (-> app :view :width)
                                          :height   (-> app :view :height)
                                   }

                     #js {
                          :position "relative"
                          })


                   :id "mainel"  :onMouseMove
                   (fn[e] (if (not @playbackmode) (on-mouse e app)))}
              ;(if @playbackmode (on-mouse e app)) (-> app :pointer :mouse-y)) ")"


              (do
                (reset! current-gui-path [])
                (@start-component    app    owner))

              (if @playbackmode
                (dom/div #js {
                            :style
                            #js {
                                 :position "absolute"
                                 :left (str (-> app :pointer :mouse-x) "px")
                                 :top (str (-> app :pointer :mouse-y) "px")
                                 :z-index 100
                                 }} "X"))

              (if @debug-mode
                (dom/div #js {
                            :style
                            #js {
                                 :margin-top "30px"
                                 }}
                  (dom/button #js {:onClick (fn [e]
                     (om/root ankha/inspector app-state
                      {:target (js/document.getElementById "playback_state")})
                          nil )} "Show UI state")

                  (dom/button #js {:onClick (fn [e]
                     (om/root ankha/inspector data-state
                      {:target (js/document.getElementById "data_state")})
                          nil )} "Show Data state")
                ))

              )))
    ;---------------------------------------------------------

))

