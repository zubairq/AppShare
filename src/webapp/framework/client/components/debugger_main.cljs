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
   [webapp.framework.client.coreclient     :only  [log remote component-fn]]
   [webapp.framework.client.system-globals :only  [debugger-ui
                                                   debug-event-timeline
                                                   app-state
                                                   app-watch-on?
                                                   data-accesses]]
   )
  (:use-macros
   [webapp.framework.client.neo4j         :only  [neo4j]]
   [webapp.framework.client.coreclient    :only  [component]]
   )
  (:require-macros
   [cljs.core.async.macros :refer [go]]))


;-----------------------------------------------------
; This changes the app state
;-----------------------------------------------------
(defn update-app-pos [debugger-state value]
  (let
    [
     current-event       (get @debug-event-timeline value)
     debug-ids           (reverse (range 1 (+ 1 (:pos @debugger-state))))
     ]
    (om/update! debugger-state [:pos] value)

    (reset! app-watch-on? false)
    (if (= (:event-type current-event) "UI")
      (reset! app-state (:value current-event))
      (do
        ;(.log js/console (str "debug count: " debug-ids))
        (let [
              new-pos   (first (drop-while (fn[event-id] (not= (get (get @debug-event-timeline event-id) :event-type) "UI"))  debug-ids))
              ;new-pos   (get (get @debug-event-timeline (first debug-ids)) :event-type)
              ]
          ;(.log js/console (pr-str "UI pos" new-pos))
          (if (pos? new-pos)
             (reset! app-state (:value (get @debug-event-timeline new-pos)))
            )
          )
        )
      )
    ;(reset! app-watch-on? true)

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
                                            (do
                                              (update-app-pos  app  (dec (get-in @app [:pos])))
                                              (om/update! app [:events-filter-path] nil))

                                          ))

                                        } "<")
                       (dom/button #js {
                                        :style #js {:display "inline-block"
                                                     :marginRight "5px"}
                                        :onClick
                                        (fn[x]
                                          (if (pos? (get-in @app [:pos]))
                                            (do
                                              (update-app-pos  app  (inc (get-in @app [:pos])))
                                              (om/update! app [:events-filter-path] nil))))

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
                                 (om/update! app [:events-filter-path] nil)
                                 ))
                             })

                       )

              (dom/div nil
               (str (-> app :pos) " of "
                    (-> app :total-events-count ))

               )

              (dom/pre #js {
                            :style #js { :fontSize "14px"}
                            :onMouseEnter #(reset! debugger-ui (assoc-in @debugger-ui [:mode] "show-event"))
                            }
                       (if (pos? (count (get @debugger-ui :react-components)))
                         (apply str  (into [] (map (fn[li] (str (get li :fn-name) "   "))
                                                   (get @debugger-ui :react-components))))
                         "Mouse over components to show code"
                         )))

              )))





;(get @data-accesses
;      {:tree "UI" :path [:ui :companies :values]})








(defn show-tree [a-tree is-map? current-path tree debugger]
  (dom/div nil
           ;------
           ;START
           ;------
           (cond

            is-map?
            (let [idt (get @data-accesses {:tree tree :path current-path})]
              (dom/div #js {:style #js {:paddingLeft "20px" :display "inline-block"
                                        :verticalAlign "top"
                                        :color (if idt "red" "")
                                        }
                            :onClick (fn[e] (if idt
                                              (om/update! debugger [:events-filter-path] current-path)
                                              ))
                            } (str
                                            (:key a-tree)
                                            )))

            (map? a-tree)
            (dom/div #js {:style #js {:paddingLeft "20px"}} "{")

            (or
             (vector? a-tree)
             (seq? a-tree)
             (list? a-tree)
             (coll? a-tree)
             )
            (dom/div #js {:style #js {:paddingLeft "20px"}} "[")
            )


           ;------
           ;VALUE
           ;------
           (cond

            is-map?
             (dom/div #js {:style #js {:paddingLeft "20px"  :display "inline-block"
                                        :verticalAlign "top"}}
              (show-tree (:value a-tree) false current-path tree debugger)
                      )

            (map? a-tree)
            (do
              (apply dom/div #js {:style #js {:paddingLeft "20px"}}
                     (map
                      #(show-tree  {:key %1 :value (get a-tree %1)} true (conj current-path %1 ) tree debugger)
                      (keys a-tree) ))
              )

            (or
             (vector? a-tree)
             (seq? a-tree)
             (list? a-tree)
             (coll? a-tree)
             )
            (apply dom/div #js {:style #js {:paddingLeft "20px"}}
                   (map #(show-tree %1 false (conj current-path %1 ) tree debugger) a-tree))


            :else
            (dom/div  #js {:style #js {:paddingLeft "20px"}}
                      (pr-str a-tree))

            )


           ;------
           ;END
           ;------
           (cond

            is-map?
            (dom/div nil "")

            (map? a-tree)
            (dom/div #js {:style #js {:paddingLeft "20px" :paddingBottom "20px"}} "}")

            (or
             (vector? a-tree)
             (seq? a-tree)
             (list? a-tree)
             (coll? a-tree)
             )
            (dom/div #js {:style #js {:paddingLeft "20px" :paddingBottom "20px" }} "]")

            )))








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
                   debug-id         (get event-item :id)
                   event-type       (get event-item :event-type)
                   old-value        (get event-item :old-value)
                   new-value        (get event-item :value)
                   event-name       (get event-item :event-name)
                   action-name      (get event-item :action-name)
                   action-input     (get event-item :input)
                   action-result    (get event-item :result)
                   component-name   (get event-item :component-name)
                   component-path   (get event-item :component-path)
                   component-data   (get event-item :component-data)
                   deleted          (first (data/diff old-value new-value))
                   added            (second (data/diff old-value new-value))
                   ]
                   (dom/div nil
                           (dom/h1
                            #js {:style #js {
                                             :color (cond
                                                     (= event-type "render")  "black"
                                                     (= event-type "UI")      "black"
                                                     (= event-type "event")   "blue"
                                                     (= event-type "DATA")    "red"
                                                     (= event-type "remote")  "red"
                                                     )
                                             }}
                            (str  debug-id ") " event-type))

                            (if deleted (dom/div #js {:style #js {:color "red"}}
                                                 (dom/div nil "Deleted")
                                                 (show-tree  deleted false [] event-type debug-ui-state)
                                                 ))

                            (if added (dom/div #js {:style #js {:color "green"}}
                                               (dom/div nil "Added")
                                               (show-tree  added false [] event-type debug-ui-state)
                                               ))




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
                                                       ))))



                           (if (= event-type "remote") (dom/div #js {:style #js {:color "red"}}

                                             (dom/div #js {:style #js {:color "blue"}} (str action-name))
                                             (dom/div #js {:style #js {:color "black"}} (show-tree action-input false [] nil debug-ui-state) )
                                             (dom/pre #js {:style #js {:color "green"}} (show-tree action-result false [] nil debug-ui-state) )
                                             ))






                            (if (= event-type "render")
                              (do
                                (if (=  debug-id (-> debug-ui-state :pos))
                                  (om/root
                                   (get (:react-components-fns @debugger-ui)
                                        (str component-name))
                                   (atom component-data)
                                   {:target (js/document.getElementById "debugger_ui_preview")}))

                              (dom/div #js {:style #js {:color "green"}}

                                       (dom/div #js {
                                                     :onClick #(do (om/update!
                                                                debug-ui-state
                                                                [:code-show_index]
                                                                (if (= (get @debug-ui-state :code-show_index) debug-id)
                                                                  nil
                                                                  debug-id))
                                                                 (om/update!
                                                                            debug-ui-state
                                                                            [:code-data-show_index] nil))
                                                                :style #js {:color "blue"
                                                                            :text-decoration "underline"
                                                                            :display "inline-block"
                                                                            }} (str
                                                                                (if (= (get debug-ui-state :code-show_index)
                                                                                       debug-id)
                                                                                  "-" "+")
                                                                                component-name
                                                                                " "
                                                                                component-path
                                                                                ))

                                       (dom/div #js {
                                                     :onClick #(do
                                                                 (om/update!
                                                                  debug-ui-state
                                                                  [:code-data-show_index]
                                                                  (if (= (get @debug-ui-state :code-data-show_index) debug-id)
                                                                    nil
                                                                    debug-id))
                                                                 (om/update!
                                                                  debug-ui-state
                                                                  [:code-show_index] nil))
                                                     :style #js {:color           "blue"
                                                                 :text-decoration "underline"
                                                                 :display         "inline-block"
                                                                 :paddingLeft     "10px"
                                                                 }
                                                     }
                                                (str
                                                 (if (= (get debug-ui-state :code-data-show_index) debug-id)
                                                   "-" "+")
                                                 "Input data"))

                                       (if (= (get debug-ui-state :code-show_index) debug-id)
                                         (dom/pre #js {
                                                       :style #js {:position "absolute" }
                                                       :onMouseLeave #(om/update! debug-ui-state [:code-show_index]
                                                                                  nil)}
                                                  (->
                                                   (str
                                                    (get
                                                     (get @debugger-ui :react-components-code)

                                                     component-name

                                                     ))
                                                   (clojure.string/replace #"\(div\ " "(DIV " )
                                                   (clojure.string/replace #"\(dom/h2\ " "(H2 " )
                                                   )))


                                       (if (= (get debug-ui-state :code-data-show_index) debug-id)
                                         (dom/pre #js {
                                                       :style #js {:position "absolute" }
                                                       :onMouseLeave #(om/update! debug-ui-state [:code-data-show_index]
                                                                                  nil)}
                                                  (show-tree  component-data  false  component-path "UI" debug-ui-state)

                                                  )))))))))))))

;(get @data-accesses {:tree "UI" :path (get @debugger-ui :events-filter-path)})
;(get @debugger-ui :events-filter-path)








(defn main-debug-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div #js {
                   :style #js {:width "100%"}
                   }
              (dom/div #js {
                            :style #js {:height "300px" :border "1px solid black"
                                        :border-radius "15px" :padding "5px"
                                        :width "100%"}
                            :onMouseEnter #(reset! debugger-ui (assoc-in @debugger-ui [:mode] "show-event"))
                            }

                       (dom/div nil  (str "Filter: "
                                          (if (get app :events-filter-path)
                                            (pr-str (get app :events-filter-path))
                                            "Show all in past"
                                            )))

                       (dom/div #js {:style #js {:height "250px" :overflow "scroll" :paddingRight "40px"}}
                                (apply dom/div nil

                                         (map
                                          (fn[x]
                                            (if x
                                              (dom/div #js {:style #js {:paddingLeft "20px"}
                                                          }
                                                     (let [thisitem      (get @debug-event-timeline x)]
                                                       (dom/pre #js {:style #js {:paddingLeft "20px"
                                                                                 :backgroundColor "darkgray"}
                                                                     :onClick (fn[e] (update-app-pos app x))
                                                                     }

                                                                (dom/div #js {:style #js {  :display         "inline-block"}}
                                                                                     (str x " "  (:event-type thisitem) " "
                                                                     (cond
                                                                      (=  (:event-type thisitem) "render")
                                                                      (str (:component-name thisitem) " " (:component-path thisitem))
                                                                      )

                                                                     ))


                                                                (cond
                                                                 (= (:event-type thisitem) "render")
                                                                 (dom/div #js {
                                                                               :onClick #(do
                                                                                           (om/update!
                                                                                            app
                                                                                            [:code-data-show_index2]
                                                                                            (if (= (get @app :code-data-show_index2) x)
                                                                                              nil
                                                                                              x))
                                                                                           (om/update!
                                                                                            app
                                                                                            [:code-show_index2] nil))
                                                                               :style #js {:color           "blue"
                                                                                           :text-decoration "underline"
                                                                                           :display         "inline-block"
                                                                                           :paddingLeft     "10px"
                                                                                           }
                                                                               }
                                                                          (str
                                                                           (if (= (get app :code-data-show_index2) x)
                                                                             "-" "+")
                                                                           "Input data")))


                                                                (if (= (get app :code-data-show_index2) x)
                                                                  (dom/pre #js {
                                                                                :style #js {:position "absolute" }
                                                                                :onMouseLeave #(om/update! app [:code-data-show_index2]
                                                                                                           nil)}
                                                                           (show-tree
                                                                            (get thisitem :component-data)  false
                                                                            (get thisitem :component-path) "UI" app)

                                                                           ))


                                                                ))

                                                     (let [thisitem      (get @debug-event-timeline x)
                                                           parentitemid  (get thisitem :parent-id)
                                                           parentitem    (if parentitemid (get  @debug-event-timeline  parentitemid))
                                                           ]

                                                       (if parentitemid
                                                         (dom/pre #js {:style #js {:paddingLeft "20px" :marginLeft "50px"
                                                                                 }
                                                                     :onClick (fn[e] (update-app-pos  app  parentitemid))}

                                                                (str
                                                                 (if parentitem (cond
                                                                                 (= (:event-type parentitem) "event")
                                                                                 (str parentitemid " " (:event-type parentitem) "::"
                                                                                      (:event-name parentitem) )

                                                                                 (= (:event-type parentitem) "render")
                                                                                 (str parentitemid " " (:event-type parentitem) " "
                                                                                      (:component-name parentitem) " "
                                                                                      (:component-path parentitem)
                                                                                      )

                                                                                 ))
                                                                 )))

                                                       )
                                                     )))

                                          (if (get app :events-filter-path)
                                            (reverse (get @data-accesses {:tree "UI" :path (get app :events-filter-path)}))
                                            (reverse
                                             (let [aps   (- (-> app :pos) 20)
                                                   act   (if (> aps 0) aps 1)
                                                   ]
                                               (into [] (range act (+ 1 (-> app :pos))))
)))))))))))






(defn details-debug-comp [app owner]
  (reify
    om/IRender
    ;---------
    (render
     [_]
     (dom/div nil
              (cond
               (= (:mode @debugger-ui) "show-event")
               (om/build  show-event-component   app))))))

;(reverse (get @data-accesses {:tree "UI" :path (get @debugger-ui :events-filter-path)}))
;(keys @debug-event-timeline )
