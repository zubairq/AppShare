(ns webapp.framework.client.system-globals
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )
)

(def record-pointer-locally (atom true))


(def start-component (atom nil))


(def playbackmode (atom false))

(def call-stack
  (atom
   []))

(def data-accesses
  (atom
   {}))

(map :path (keys @data-accesses))


(def data-state
  (atom
   {}))

(def app-state
  (atom
   {}))

;-----------------------------------------------------
;  This is the application watch space. So whenever
; the application changes then we record the event
;-----------------------------------------------------
(def app-watch-on? (atom true))




(def ui-watchers (atom []))

(def data-watchers (atom []))

(def ab-tests (atom {}))

(def ab-goals (atom {}))


(def init-state-fns (atom []))



(defn subtree-different? [orig-val new-val path]
  (let [
        orig-subset    (get-in orig-val  path)
        new-subset     (get-in new-val   path)
        ]
      (not (identical?  orig-subset  new-subset))))







;@app-state

(def blank-app-state
  {
   :data {
          :width "50"
          }
   :pointer
   {
    :mouse-x 0
    :mouse-y 0
    }
   :view
   {
    :width   0
    :height  0
    }
   }
  )

;(reset! app-state (assoc-in @app-state [:data :width ] "20"))


(defn reset-app-state []
  (reset!  app-state  blank-app-state))




(def init-fn (atom nil))




(def playback-app-state
  (atom
   {}
   ))




(def playback-controls-state
  (atom
   {:ui
    {
     :current-session   nil
     :current-page      0
     :delete-password {:label "Delete password"
                       :placeholder ""
                       :value ""}

     }
    :data {
           :sessions          []
           }
    }

   ))

(defn reset-playback-app-state []
  (reset!  playback-app-state  blank-app-state))



(defn  update-data [path value]
   (reset! data-state (assoc-in @data-state path value)))

(defn  data-tree! [path value]
   (reset! data-state (assoc-in @data-state path value)))


(defn  data-tree [path]
  (get-in @data-state path))


(defn  -->data [path value]
   (reset! data-state (assoc-in @data-state path value)))


(defn  <--data [path]
  (get-in @data-state path))



(defn add-init-state-fn [nm init-state-fn]
  (do
    ;(.log js/console (str "Init function added: " nm))
    (swap!  init-state-fns conj init-state-fn)))


(defn get-in-tree [app path]
  (get-in @app path))


(defn  set-ab-tests [tree]
  (do
    (reset! ab-tests tree)
))



(defn  set-ab-goals [tree]
   (reset! ab-goals tree))




(def touch-id  (atom 0))

(defn touch [path]
    (reset! app-state (assoc-in @app-state path
                                (assoc
                                  (get-in @app-state path)
                                  :touch-id
                                  (swap! touch-id inc)
                                  ))))



(def debug-event-timeline (atom {}))
;(map #(str %1) @debug-event-timeline)
;(keys (get @debug-event-timeline 49))





(def component-usage (atom
                      {}
                      ))

(def debugger-ui
  (atom {
         :mode                     "show-event"
         :react-components         []
         :react-components-code    {}
         :watchers-code            {}
         :pos                      1
         :total-events-count       0
         :events-filter-path       nil
         }))



(def debug-count (atom 0))
(defn add-debug-event [& {:keys [
                                 event-type
                                 old
                                 new
                                 error
                                 event-name
                                 component-name
                                 component-data
                                 component-path
                                 action-name
                                 input
                                 result
                                 parent-id
                                 ] :or {
                                        event-type     "UI"
                                        error          "Error in field"
                                        }}]

  (if

    (and
     (or
      @record-pointer-locally
      (not (and (= event-type "UI") (get (first (data/diff old new)) :pointer)))
      )
     (not (= (get action-name 0) "!"))
     @app-watch-on?
     )


    (do

      (let [debug-id (swap! debug-count inc)]
        (cond



         (or (first (data/diff old new)) (second (data/diff old new)))

         (swap! debug-event-timeline assoc
                debug-id  {
                           :id          debug-id
                           :event-type  event-type
                           :old-value   old
                           :value       new
                           :parent-id   parent-id
                           })




         (and (= event-type     "event"))

         (do
           (swap! debug-event-timeline assoc
                  debug-id  {
                             :id          debug-id
                             :event-type  event-type
                             :event-name  event-name
                             }))

         (and (= event-type     "render"))

         (let [component-id {:fn-name component-name :fn-path component-path}]
           (swap! debug-event-timeline assoc
                  debug-id  {
                             :id              debug-id
                             :event-type      event-type
                             :component-name  component-name
                             :component-path  component-path
                             :component-data  component-data
                             })

           (if (get @component-usage  component-id)
             (reset! component-usage (assoc @component-usage component-id
                                       (conj (get @component-usage component-id) debug-id)))
             (reset! component-usage (assoc @component-usage component-id [debug-id]))
             )
           )



         (and (= event-type     "remote"))
         (do
           (swap! debug-event-timeline assoc
                  debug-id  {
                             :id              debug-id
                             :event-type      event-type
                             :action-name     action-name
                             :input           input
                             :result          result
                             }))


         )


        (reset! debugger-ui
                (assoc @debugger-ui
                  :total-events-count (count @debug-event-timeline)))

        (if (> (+ (:pos @debugger-ui) 5) (:total-events-count @debugger-ui))
          (reset! debugger-ui
                  (assoc @debugger-ui
                    :pos (:total-events-count @debugger-ui))))

        (swap! call-stack conj debug-id)
        debug-id
        )

      )))

(defn remove-debug-event [did]
  (reset! call-stack
          (into [] (filter #(not= %1 did) @call-stack))
          )
  )












;-----------------------------------------------------
; This is when the user moves the timeline slider
; left and right. If the slider is currently being moves
; (ie: not at the right of the slider - meaning the
; latest position) then turn off the events capture
; from the application
;
; We do this because otherwise the application keeps
; receiving events otherwise
;-----------------------------------------------------
(def data-and-ui-events-on? (atom true))
(add-watch debugger-ui
           :change-debugger-ui

           (fn [_ _ old-val new-val]

             (cond
               (or
                (= js/debug_live false)
                (= (new-val :pos) (new-val :total-events-count))
                )
               (reset! data-and-ui-events-on? true)

              :else
               (reset! data-and-ui-events-on? false)
             )
             ;(. js/console log (pr-str new-val))

             )
           )

(defn contains-touch-id? [x]
  (cond
   (vector? x)
   (some #(if (contains-touch-id? %1) true) x)

  (map? x)
  (if (get x :touch-id)
    true
    (some #(contains-touch-id? %1) (vals x))
    )


  ))


;(contains-touch-id? [{:a {:touch-id 1}}])
;(+ (:pos @debugger-ui ) 5)
;(:total-events-count @debugger-ui )
;(get @debug-event-timeline 20)


(add-watch app-state
           :change
           (fn [_ _ old-val new-val]
             (do
               ;(.log js/console (pr-str  new-val))
               (if (and @app-watch-on? (not (contains-touch-id?  new-val)))
                 ;(if @app-watch-on?
                 (comment let [debug-id (add-debug-event
                                 :event-type  "UI"
                                 :old         old-val
                                 :new         new-val
                                 :parent-id   (last @call-stack)
                                 )]
                   (remove-debug-event debug-id)
                   )))))

(add-watch data-state
           :change
           (fn [_ _ old-val new-val]
             (if @app-watch-on?
               (let [debug-id (add-debug-event
                               :event-type  "DATA"
                               :old         old-val
                               :new         new-val
                               :parent-id   (last @call-stack)
                               )]
                 (remove-debug-event debug-id)))))





(comment add-debug-event
                :event-type  "event"
                :event-name  "watch-ui [:ui :request :to-email :value]"
                )

;(map :event-type (vals @debug-event-timeline))


; (:events-filter-path @debugger-ui)

(def gui-calls (atom {}))
(def current-gui-path (atom []))

;(filter #(= (:fn-name %1) "text-graph") @component-usage)

;@component-usage


;@call-stack

;(keys @gui-calls)


;(get @data-accesses (first (keys @data-accesses)))

@data-accesses
