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


;-----------------------------------------------------
; This changes the app state
;-----------------------------------------------------
(defn update-app-pos [debugger-state value]
  (let
    [current-event (get @debug-event-timeline value)]
    (om/update! debugger-state [:pos] value)

    (reset! app-watch-on? false)
    (if (= (:event-type current-event) "UI")
      (reset! app-state (:value current-event)))
    (reset! app-watch-on? true)


    (om/update! debugger-state
            [:mode] "show-event")


))


(defn main-debug-slider-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div #js {
                             :onMouseEnter #(om/update! app [:mode] "show-event")
                             }
              (dom/div nil
                       (dom/button #js {
                                        :style #js {:display "inline-block"
                                                     :marginRight "5px"}
                                        :onClick
                                        (fn[x]
                                          (if (pos? (get-in @app [:pos]))
                                            (update-app-pos  app  (dec (get-in @app [:pos])))

                                          ))

                                        } "<")
                       (dom/button #js {
                                        :style #js {:display "inline-block"
                                                     :marginRight "5px"}
                                        :onClick
                                        (fn[x]
                                          (if (pos? (get-in @app [:pos]))
                                            (update-app-pos  app  (inc (get-in @app [:pos])))

                                          ))

                                        } ">")
                       (dom/input
                        #js {
                             :style #js {:display "inline-block" :width "80%"
                                         }

                             :value (str (-> app :pos))
                             :type "range"
                             :min  "1"
                             :max  (str (-> @debugger-ui :total-events-count ))
                             :onChange
                             (fn[e]
                               (let [value (js/parseInt (.. e -target -value))]
                                 (update-app-pos   app value)
                                 ))
                             })

                       )

              (dom/div nil
               (str (-> app :pos) " of "
                    (-> app :total-events-count ))

               )))))

(range 1 10)


(defn show-event-component[ debug-ui-state  owner ]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (apply dom/div nil
            (for [event-item (into []
                              (map (fn[xx] (get @debug-event-timeline xx))
                                   (reverse
                                    (into [] (range (- (-> debug-ui-state :pos) 20) (+ 1 (-> debug-ui-state :pos))))
                                   )))
                              ]
              (if event-item
                (let
                  [
                   debug-id    (get event-item :id)
                   event-type  (get event-item :event-type)
                   old-value   (get event-item :old-value)
                   new-value   (get event-item :value)
                   event-name  (get event-item :event-name)
                   deleted     (first (data/diff old-value new-value))
                   added       (second (data/diff old-value new-value))
                   ]
                   (dom/div nil
                           (dom/h1 nil (str event-type " " debug-id))

                           (if deleted (dom/div #js {:style #js {:color "red"}}
                                    (dom/div nil "Deleted")
                                    (dom/div nil

                                             (str

                                              (pr-str (if deleted  deleted  "Nothing deleted"))))))

                           (if added (dom/div #js {:style #js {:color "green"}}
                                    (dom/div nil "Added")
                                    (dom/div nil

                                             (str

                                              (pr-str (if added added "Nothing added"))))))

                           (if (= event-type "event") (dom/div #js {:style #js {:color "green"}}

                                             (dom/div #js {:style #js {:color "blue"}} (str event-name))
                                             (dom/pre nil

                                                      (->
                                                       (str
                                                       (get
                                                        (get @debugger-ui :watchers-code)

                                                        event-name

                                                        ))
                                                       (clojure.string/replace #"\(div\ " "(DIV " )
                                                       )

                                                      )
                                             ))



                           ))))))))









(defn main-debug-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div #js {
                             :style #js {:height "300px"}
                             :onMouseEnter #(reset! debugger-ui (assoc-in @debugger-ui [:mode] "show-event"))
                             }

              (cond
               (= (:mode @debugger-ui) "browse")
                (dom/h2 #js {
                             :style #js {:height "100%"}
                             :onMouseEnter #(reset! debugger-ui (assoc-in @debugger-ui [:mode] "show-event"))
                             } (str (get @debugger-ui :react-components)))


               (= (:mode @debugger-ui) "show-event")
               (om/build  show-event-component   app)


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
                                 ))


                        (dom/button #js {
                                         :onClick
                                         (fn[x](om/transact! app [:mode]
                                                        #(str "browse")))

                                         } "Back"))

              )))))




