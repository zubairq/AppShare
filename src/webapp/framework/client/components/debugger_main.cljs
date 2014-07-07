(ns webapp.framework.client.components.debugger-main
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
   [webapp.framework.client.coreclient     :only  [log remote]]
   [webapp.framework.client.system-globals :only  [debugger-ui
                                                   debug-event-timeline
                                                   app-state
                                                   app-watch-on?]]
   )
  (:use-macros
   [webapp.framework.client.neo4j      :only  [neo4j]]
   )
  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(defn main-debug-slider-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div nil
              (dom/input
               #js {:value (str (-> app :pos))
                    :type "range"
                    :min  "1"
                    :max  (str (-> @debugger-ui :total-events-count ))
                    :onChange
                    (fn[e]
                      (let [value (js/parseInt (.. e -target -value))]
                        (do
                        (om/update! app [:pos]
                                                        value)

                        (reset! app-watch-on? false)
                        (reset! app-state
                                (:value (get @debug-event-timeline value))
                                )
                          (reset! app-watch-on? true)

                        )))
                    })

              (dom/div nil
               (str (-> app :pos) " of "
                    (-> app :total-events-count ))

               )))))




(defn main-debug-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div nil

              (cond
               (= (:mode @debugger-ui) "browse")
                (dom/h2 nil (str (get @debugger-ui :react-components)))



               (= (:mode @debugger-ui) "component")
               (dom/h2 nil
                       (dom/div nil (:current-component @debugger-ui))
                       (dom/button #js {
                                         :onClick
                                         (fn[x](om/transact! app [:mode]
                                                        #(str "browse")))

                                         } "Back")
                       (dom/pre nil
                                (->
                                 (get
                                 (get @debugger-ui :react-components-code)

                                 (:current-component @debugger-ui)

                                 )
                                 (clojure.string/replace #"\(div\ " "(DIV " )
                                 (clojure.string/replace #"\(div\r" "(DIV \r" )
                                 (clojure.string/replace #"\(div\n" "(DIV \n" )
                                 (clojure.string/replace #":onClick" ":ONCLICK" )
                                 (clojure.string/replace #":onTouchStart" ":ONTOUCHSTART" )
                                 ))


                        (dom/button #js {
                                         :onClick
                                         (fn[x](om/transact! app [:mode]
                                                        #(str "browse")))

                                         } "Back"))

              )))))




