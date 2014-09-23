(ns webapp.framework.client.components.system-container
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
   [webapp.framework.client.coreclient           :only  [log remote-fn debug-mode component-fn
                                                         remove-debug-event
                                                         ]]
   [webapp.framework.client.system-globals       :only  [app-state
                                                         playbackmode
                                                         ui-watchers
                                                         data-watchers
                                                         start-component
                                                         data-state
                                                         ab-tests
                                                         init-state-fns
                                                         data-and-ui-events-on?
                                                         add-debug-event
                                                         ]]
   )
  (:use-macros
   [webapp.framework.client.coreclient :only  [defn-ui-component ns-coils div component remote]]
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







(def ui-events (atom []))
(def data-events (atom []))






(defn add-as-watch [the-ref  tree-name  watchers   args  ch]

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
                             (do
                               ;(apply (:fn watch) args)
                               ;(go (>! ch {:watch watch :extra []}))
                               (swap! ch conj {:watch watch :extra []})
                               (swap! app-state assoc :touch-id (rand-int 99999))
                               (remove-debug-event  debug-id)

                               )))



                         (= (:type watch) "value change")
                         ;-------------------------------
                         (let [debug-id
                               (add-debug-event
                                :event-type  "event"
                                :event-name  (str "watch-" tree-name " " (:path watch))
                                )]
                             (do
                               ;(js/alert (str "watch-" tree-name " " (:path watch)))
                               ;(apply (:fn watch) args)
                               ;(go (>! ch {:watch watch :extra []}))
                               (swap! ch conj {:watch watch :extra []})
                               (swap! app-state assoc :touch-id (rand-int 99999))
                               (remove-debug-event  debug-id)))



                         (= (:type watch) "record property equals")
                         ;-----------------------------------------
                         (let [records (filter
                                        (fn [r] (=  (get r (:field watch)) (:value watch)))
                                        (get-in new-val (:path watch))
                                        )]
                           (if (pos? (count records))
                             (do
                               ;(apply (:fn watch) (conj args records))
                               ;(go (>! ch {:watch watch :extra records}))
                               (swap! ch conj {:watch watch :extra records})
                               (swap! app-state assoc :touch-id (rand-int 99999))
                               ;nil
                               )))



                         ))


                      :else
                      nil ))

                  )))))




(defn swap*!
  "Like swap! but returns a vector of [old-value new-value]"
  [atomic-value f & args]
  (loop []
    (let [ov @atomic-value
          nv (apply f ov args)]
      (if (compare-and-set! atomic-value ov nv)
        [ov nv]
        (recur)))))

(defn remove-first-and-return [atomic-value]
  (let [x
        (let [[ov nv] (swap*! atomic-value subvec 1)]
          (try
            (first ov)
            (catch :default error
              (do nil))))]
    x))


(defn pop-q [a]
  (try
  (remove-first-and-return   a)
  (catch :default e
    nil)))


(def a (atom [123 2]))






(defn main-view [app owner]
  (reify
    ;---------------------------------------------------------
    om/IWillMount
    (will-mount [_]
                (do
                  ; set up the initial state
                  (if (not @playbackmode)
                           (dorun (for [init-state-fn  @init-state-fns]
                                    (do
                                      (init-state-fn)
                                      ))))

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
                  (let [
                        ui-chan   (chan)
                        data-chan (chan)
                        ]
                    (add-as-watch   app-state
                                    "ui"
                                    ui-watchers
                                    [app]
                                    ui-events)




                    (add-as-watch   data-state
                                    "data"
                                    data-watchers
                                    [app]
                                    data-events)





                    (comment (go (loop []
                          (let [called-ui (pop-q  ui-events)]
                            (if called-ui
                              (apply (:fn (:watch called-ui)) (conj [app] (:extra called-ui))))
                            (recur))))

                    (go (loop []
                          (let [called-data (pop-q  data-events)]
                            (if data-events
                              (apply (:fn (:watch called-data)) (conj [app] (:extra called-data))))
                            (recur)))


                        ))

                      )

                  ))

    ;---------------------------------------------------------
    om/IRenderState
    (render-state
     [this state]

     (do

       (loop [called-ui (pop-q  ui-events)]
         (if (nil? called-ui)
           nil
           (do
             (apply (:fn (:watch called-ui)) (conj [app] (:extra called-ui)))
             (recur (pop-q  ui-events)))))

         (loop [called-data (pop-q  data-events)]
         (if (nil? called-data)
           nil
           (do
             (apply (:fn (:watch called-data)) (conj [app] (:extra called-data)))
             (recur (pop-q  data-events)))))



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
                             (let [path []]
                               (component    @start-component app  [])
                               )
                             )

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

                           ))))
    ;---------------------------------------------------------

))

