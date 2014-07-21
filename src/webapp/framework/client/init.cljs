(ns webapp.framework.client.init
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
   [webapp.framework.client.coreclient      :only  [log remote component-fn]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    set-ab-tests
                                                    set-ab-goals
                                                    ]]
   [webapp.framework.client.components.main                    :only   [main-view]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]])

   (:use-macros
     [webapp.framework.client.coreclient    :only  [component  ns-coils]])

  )

(ns-coils 'webapp.framework.client.init)







(defn  ^:export setup []

  (reset!
   app-state

   (assoc-in
    @app-state [:ui]
    {


     }))


  (reset! data-state {
                      :submit {}
                      })






  (set-ab-tests {
                 })




  )






(defn ^:export main [app owner]
  (let [path []]
    (component  main-view  app  [])))

