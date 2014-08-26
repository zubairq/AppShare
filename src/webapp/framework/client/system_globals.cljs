(ns webapp.framework.client.system-globals
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )
)

















(def record-pointer-locally
  "
 This determines whether the mouse pointer events are
 recorded for the local Coils debugger. For an example
 of mouse pointer events looks at the playback of a Coils
 app where you can see where the website user moved the
 mouse. In the React/Om UI tree this is stored in:

  {
     :pointer
     {
         :mouse-x 0
         :mouse-y 0
     }
  }


 To enable or disable this you must be specify
 it as a global setting in system_globals.clj as

 (defonce ^:dynamic *record-pointer-locally* false)
  "
  (atom true))









(def start-component
  "
 This is a reference to the first component of a Coils
 app that must be loaded


 It is actually specified in:

  src/webapp/framework/client/main/main.cljs

 :in the functions:

  load_main
  load_admin
  load_debug

 :These are called from the first webpage that is loaded
  in coils. See the file:

  resources/public/main.html

  : and you will find:

   webapp.framework.client.main.load_main(
                                    webapp.framework.client.init.setup);

  Looking at:

  webapp.framework.client.init/setup

  :you will see:


(def  ^:export setup
  {
   :start-component
   main-view

   :setup-fn
   (fn[]
     (do
      ...

  "

  (atom nil))







(def playbackmode
  "
  If set to true then this means that the user interface is being replayed based on
  the server side recording
  "
  (atom false))





(def call-stack
  "
  This is supposed to be a vector which lists the debug IDs of what has
  been called, eg:

  [1 45 99 103]
  "
  (atom
   []))







(def data-accesses
  "
  This shows where the UI and DATA trees change. The list contains
  the debug IDs of where an action reads or writes this part of
  the tree

  {
      {:tree UI, :path [:ui :companies :values]}
          [7 14 18 nil nil nil nil]

      {:tree UI, :path [:ui :latest-endorsements :values]}
          [30]

      {:tree UI, :path [:ui :splash-screen :click]}
          [37]

      {:tree UI, :path [:ui :splash-screen :show]}
          [36]

      {:tree UI, :path [:values]}
          [nil nil nil nil nil nil nil]
  }

  Some of these seem erroneous, like:

  - nil values should never be there
  - {:tree UI, :path [:values]} does not even exist

  "
  (atom
   {}))


@ data-accesses





(def data-state
  "
  The data tree
  "
  (atom
   {}))




(def app-state
  "
  The UI tree
  "
  (atom
   {}))





(def app-watch-on?
  "
 Whenever the application UI changes then we record the event,
 unless the app-watch-on? is set to false
  "
  (atom true))





(def ui-watchers
  "
  This is a list of all the code that waits for things to happen
  on the UI tree
  "
  (atom []))






(def data-watchers
  "
  This is a list of all the code that waits for things to happen
  on the DATA tree
  "

  (atom []))




(def ab-tests
  "
  The list of all the AB tests
  "
  (atom {}))






(def ab-goals
  "
  The list of all the AB goals
  "
  (atom {}))






(def init-state-fns
  "
  This holds all the fns to call at the start of the
  webapp
  "
  (atom []))



(defn subtree-different?
  "
  This checks to see if a subtree of a tree is
  different
  "
  [orig-val new-val path]
  (let [
        orig-subset    (get-in orig-val  path)
        new-subset     (get-in new-val   path)
        ]
      (not (identical?  orig-subset  new-subset))))








(def blank-app-state
  "
  The initial starting state for the UI tree
  "
  {
   :data
   {
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







(defn reset-app-state
  "
  Resets the UI to be blank
  "
  []
  (reset!  app-state  blank-app-state))








(def init-fn
  "
  An Initialise function called when the web app first starts up
  "
  (atom nil))









(def playback-app-state
  "
  This holds the UI state of the web applications when
  it is being played back. I'm not sure why we didn't just
  use the normal app state for this?
  "
  (atom
   {}
   ))




(def playback-controls-state
  "
  The react/Om UI state for the debugger
  "
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







(defn reset-playback-app-state
  "
  Reset the app state for playback using the initial blank state
  "
  []
  (reset!  playback-app-state  blank-app-state))








(defn  update-data
  "
  Updates the data tree
  "
  [path value]
   (reset! data-state (assoc-in @data-state path value)))










(defn add-init-state-fn
  "
  "
  [nm init-state-fn]
  (do
    ;(.log js/console (str "Init function added: " nm))
    (swap!  init-state-fns conj init-state-fn)))















(defn  set-ab-tests
  "
  "
  [tree]
  (do
    (reset! ab-tests tree)
))








(defn  set-ab-goals
  "
  "
  [tree]
   (reset! ab-goals tree))










(def touch-id
  "
  "
  (atom 0))







(defn touch
  "
  "
  [path]
    (reset! app-state (assoc-in @app-state path
                                (assoc
                                  (get-in @app-state path)
                                  :touch-id
                                  (swap! touch-id inc)
                                  ))))









(def debug-event-timeline
  "
  "
  (atom {}))
;(map #(str %1) @debug-event-timeline)
;(keys (get @debug-event-timeline 49))










(def component-usage
  "
  "
  (atom
                      {}
                      ))







(def debugger-ui
  "
  "
  (atom {
         :mode                     "show-event"
         :react-components         []
         :react-components-code    {}
         :watchers-code            {}
         :pos                      1
         :total-events-count       0
         :events-filter-path       nil
         }))









(def debug-count
  "
  "
  (atom 0))







(defn add-debug-event
  "
  "
  [& {:keys [
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
))







(defn contains-touch-id?
  "
  Does the current structure contain a touch ID?
  "
  [x]
  (cond
   (vector? x)
   (some #(if (contains-touch-id? %1) true) x)

  (map? x)
  (if (get x :touch-id)
    true
    (some #(contains-touch-id? %1) (vals x))
    )))


;(contains-touch-id? [{:a {:touch-id 1}}])













(comment add-debug-event
                :event-type  "event"
                :event-name  "watch-ui [:ui :request :to-email :value]"
                )

;(map :event-type (vals @debug-event-timeline))


; (:events-filter-path @debugger-ui)





(def gui-calls
  "
  A UI state history of all the GUI components

  {
      'main-view: []'
            ....

      'splash-screen: [:ui :splash-screen]'
            [
               {:show true}
               {:show false, :click false}
               {:show true}
             ]

      'letter-a: [:ui :splash-screen]'
            ....

      'letter-a: [:ui]'
            ....

      'browser-menu: []'
            ....

      'text-graph: [:ui :companies]'
           [
               nil
               {:values [{company apple.com inbound 3}
                         {company were.com inbound 1}
                         ]}
           ]
      'browser-menu:'
            ....

      'text-graph:'
            ....
  }

  This can be removed as it is not used by the system
  anywhere
  "
  (atom {}))

 ;(get @gui-calls "text-graph: [:ui :companies]")






