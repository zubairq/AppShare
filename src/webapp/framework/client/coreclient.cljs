(ns webapp.framework.client.coreclient
  (:refer-clojure :exclude [val empty remove find next parents])
  (:require
   [clojure.string]
   [goog.net.XhrIo          :as xhr]
   [clojure.browser.repl    :as repl]
   [cljs.reader             :as reader]
   [goog.dom]
   [om.core                 :as om :include-macros true]
   [clojure.data            :as data]
   [om.dom                  :as dom]
   [clojure.zip]
   [goog.Uri.QueryData]
   [goog.events]
   [cljs.core.async         :as async :refer [chan close!]]
   )

  (:require-macros
   [cljs.core.async.macros :refer [go alt!]])

  (:use
   [webapp.framework.client.records    :only  [NeoNode map->NeoNode]]
   [clojure.browser.event :only [listen]]
   [webapp.framework.client.system-globals  :only  [touch
                                                    debugger-ui
                                                    record-pointer-locally
                                                    app-state
                                                    reset-app-state
                                                    ui-watchers
                                                    call-stack
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    add-debug-event
                                                    component-usage
                                                    gui-calls
                                                    app-watch-on?
                                                    data-accesses
                                                    paths-for-refresh
                                                    ]]))





(def debug-mode (atom false))






(defn  data-tree!
  "
  Updates the data tree
  "
  [path value]
   (reset! data-state (assoc-in @data-state path value)))








(defn  data-tree
  "
  "
  [path]
  (get-in @data-state path))








(defn  -->data
  "
  "
  [path value]
   (reset! data-state (assoc-in @data-state path value)))








(defn  <--data
  "
  "
  [path]
  (get-in @data-state path))






(defn remove-debug-event
  "
  "
  [did]
  (reset! call-stack
          (into [] (filter #(not= %1 did) @call-stack))
          )
  )



;-----------------------------------------------------
; watch when the data changes
;
;
;-----------------------------------------------------
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






(defn make-js-map
  "makes a javascript map from a clojure one"
  [cljmap]
  (let [out (js-obj)]
    (doall
     (map
      #(aset out (name (first %)) (second %)) cljmap))
    out))







(defn clj-to-js
  "Recursively transforms ClojureScript maps into
  Javascript objects, other ClojureScript colls
  into JavaScript arrays, and ClojureScript keywords
  into JavaScript strings."
  [x]

  (clj->js x))





(defn encode-parameter [name value]
  (.
   (goog.Uri.QueryData/createFromMap
    (goog.structs.Map.
     (make-js-map
      { name value}
      )
     )
    )
   (toString)
   ))




(defn get-time [] (. (new js/Date)  (getTime)))



(defn log [s]
  (.log js/console (str s))
  nil)


(defn ok [response]
  (not (:error response)))



(defn send-request2 [ address   action  parameters-in  ch]
  (let
    [
     headers       (goog.structs.Map.)
     io-object     (goog.net.XhrIo.)
     ]
      (do
      (goog.events.listen
       io-object
       goog.net.EventType.COMPLETE

       (fn [event]
         (let
           [target          (.-target event)
            status          (. target (getStatus))]
           (if (= status 200)
             (let [
                   response-text   (. target (getResponseText))
                   response        (reader/read-string response-text)
                   ]
                (let [
                      debug-id (add-debug-event
                                :event-type  "remote"
                                :action-name (str action)
                                :input       parameters-in
                                :result      response
                         )]

               (go
                  (>! ch response)
                  (close! ch))
                  (remove-debug-event  debug-id)
                  ))

              (let [debug-id
                    (add-debug-event
                     :event-type  "remote"
                     :action-name (str action)
                     :input       parameters-in
                     :result      (str "ERROR IN RESPONSE, HTTP : " status)
                     )]
             (go
                (>! ch  {:error "true"})
                (close! ch))
                (remove-debug-event  debug-id)
                )



             ))))
      (. headers set "charset" "UTF-8")
      (. io-object send address "POST" nil headers)
    ch)))







(defn remote-fn
  ([action]
   (remote-fn action {}))



  ([action  parameters-in]
   (let
     [
      parameters  (if parameters-in {:params parameters-in :tclock (get-time)})
      ch          (chan)
      ]
     (send-request2
      (str

       (if (= (first action) "!") "action?systemaction=" "action?action=" )
       action
       "&"
       (apply
        str
        (map
         (fn [x]
           (do
             (str
              (encode-parameter
               x
               (get parameters x)) "&" ))
           )
         (keys parameters))))
      action
      parameters-in
      ch
      ))))










(defn sql-fn [sql-str params]
  (go
    (<! (remote-fn
                "!sql" {:sql sql-str :params params}))))




(defn neo4j-fn [cypher-str params]
  (go
    (<! (remote-fn
                "!neo4j" {:cypher cypher-str :params params}))))




(go
 (let [env (:value (<! (remote-fn "!get-environment" {})))]
   (if (= env "dev")
     (reset! debug-mode true))))



(go
 (let [record-pointer-locally-value (:value
                                     (<! (remote-fn "!get-record-pointer-locally" {})))]
     (reset! record-pointer-locally
             record-pointer-locally-value)))









(defn GET [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go
                      (>! ch res)
                      (close! ch)))))
    ch))







(defn- xml-str
 "Like clojure.core/str but escapes < > and &."
 [x]
  (-> x str
      ;(clojure.string/replace #" " "  " )
      (clojure.string/replace #"&amp;" "&" )
      (clojure.string/replace #"&lt;" "<")
      (clojure.string/replace #"&gt;" ">" )))






(defn record-path= [namespace-name  path  value  tree-name  &  code]
  (let [
        code-str
        (str (apply str (map #(if (= "\n" %1) (str "\r\n")  %1) code)))
        ]

    (reset!
     webapp.framework.client.system-globals/debugger-ui
     (assoc-in
      (deref webapp.framework.client.system-globals/debugger-ui)
      [:watchers-code (str "==" tree-name " " path " " value) ]
      (xml-str (str
                "(ns  " namespace-name ")"
                (char 13) (char 13)


                "(==" tree-name " " path "  " value
                     (char 13) (char 13)
                     code-str
                     ""
                     ))))))






(defn record-watcher [namespace-name path tree-name & code]
  (let [
        code-str
        (str (apply str (map #(if (= "\n" %1) (str "\r\n")  %1) code)))
        ]

    (reset!
     webapp.framework.client.system-globals/debugger-ui
     (assoc-in
      (deref webapp.framework.client.system-globals/debugger-ui)
      [:watchers-code (str "watch-" tree-name " " path) ]
      (xml-str (str
                "(ns  " namespace-name ")"
                (char 13) (char 13)


                "(watch-" tree-name " " path "  "
                     (char 13) (char 13)
                     code-str
                     ""
                     ))))))





(defn record-defn-ui-component [namespace-name fn-pointer fname args & code]
  (let [
        code-str
        (str (apply str (map #(if (= "\n" %1) (str "\r\n")  %1) code)))
        ]

    ;(.log js/console (str "NAMESPACE: "            namespace-name))
    ;(.log js/console (str "NAMESPACE fname: "      fname))
    ;(.log js/console (str "NAMESPACE orig code: "  code))
    ;(.log js/console (str "NAMESPACE code: "       code-str))

    (reset!
     webapp.framework.client.system-globals/debugger-ui
     (assoc-in
      (deref webapp.framework.client.system-globals/debugger-ui)
      [:react-components-code (str fname)]
      (xml-str (str
                "(ns  " namespace-name ")"
                (char 13) (char 13)


                "(defn-ui-component  "
                     fname "  "
                     args (char 13) (char 13)
                     code-str
                     ""
                     ))))


    (reset!
     webapp.framework.client.system-globals/debugger-ui
     (assoc-in
      (deref webapp.framework.client.system-globals/debugger-ui)
      [:react-components-fns (str fname)]
      fn-pointer))


    ))


(comment
  (record-defn-ui-component nil
   "a.b" "start" '[a b] '(def 1)))









(defn process-ui-component [fn-name]
  ;(js/alert @paths-for-refresh)
  (let [paths (get @paths-for-refresh (str fn-name))]
    (if paths
      (do
        (map
         (fn [path]
           (if (get-in @app-state path) (touch  path)))
         paths
         )
        ))))





@paths-for-refresh


(get @paths-for-refresh "main-yazz-header")


(defn  ^:export loadDebugger []
  (do
   (reset! app-watch-on? false)

    (om/root
     webapp.framework.client.components.debugger-main/main-debug-comp
     debugger-ui
     {:target (js/document.getElementById "right_of_main")})


    (om/root
     webapp.framework.client.components.debugger-main/details-debug-comp
     debugger-ui
     {:target (js/document.getElementById "debugger_details")})

    (om/root
     webapp.framework.client.components.debugger-main/main-debug-slider-comp
     debugger-ui
     {:target (js/document.getElementById "main_playback_slider")})))






(defn  ^:export unloadDebugger []
  (do
    (reset! debugger-ui

            (assoc-in
             @debugger-ui
             [:react-components] []))


    (reset! debugger-ui
            (assoc-in
             @debugger-ui
             [:current-component] nil))


    (reset! debugger-ui
            (assoc-in
             @debugger-ui
             [:mode] "show-event"))


    (om/root
     (fn [app owner] (om/component (dom/div nil "")))
     debugger-ui
     {:target (js/document.getElementById "right_of_main")})))

;(unloadDebugger)







(defn display-debug-code []
  (let [component-name  (last (get @debugger-ui :react-components))]
    (reset! debugger-ui
            (assoc-in @debugger-ui [:current-component]
                      component-name))))



;(get @debugger-ui :current-component)




(defn component-clicked [x]
  (if js/debug_live
    (do
      (reset! debugger-ui (assoc-in @debugger-ui [:mode]              "show-event"))
      (reset! debugger-ui (assoc-in @debugger-ui [:current-component] (last (get @debugger-ui :react-components))))

      (if (get @component-usage  (get @debugger-ui :current-component))
        (do
          (reset! debugger-ui (assoc-in @debugger-ui [:mode] "show-event"))

          (reset! debugger-ui (assoc-in @debugger-ui [:pos]
                                        (first
                                         (drop-while
                                          (fn[xx] (> xx
                                                     (get-in @debugger-ui [:pos])
                                                     ))
                                          (reverse (get @component-usage (get @debugger-ui :current-component))))
                                         ))))))))


;@component-usage
;(get-in @debugger-ui [:pos])
;(get @debugger-ui :current-component)
;(get @component-usage (get @debugger-ui :current-component))


(defn set-debug-component [component-name  component-path]
  (let [component-identifier   {:fn-name component-name :fn-path component-path}]
    (if (not-any? #(= %1 component-identifier) (get @debugger-ui :react-components))
      (reset! debugger-ui
              (assoc-in @debugger-ui [:react-components]
                        (conj (get @debugger-ui :react-components)
                              component-identifier
                              )))
      (reset! debugger-ui
              (assoc-in @debugger-ui [:mode]
                        "show-event"))
      (display-debug-code)
      )))








(defn unset-debug-component [component-name   component-path]
  (let [component-identifier   {:fn-name component-name :fn-path component-path}]
    (reset! debugger-ui

            (assoc-in
             @debugger-ui
             [:react-components]
             (into []
                   (filter #(not= %1 component-identifier)
                           (get @debugger-ui :react-components))
                   ))
            )
    (display-debug-code)
    ))







(defn debug-react [str-nm owner data react-fn path parent-id]
  (let
    [
     react-fn-name    (str str-nm)
     ]
      (do

        (dom/div
       #js {
            :onMouseEnter #(if js/debug_live (om/set-state! owner :debug-highlight true))

            :onMouseLeave #(if js/debug_live (om/set-state! owner :debug-highlight false))

            :onClick component-clicked

            :style (if js/debug_live
                     #js {:backgroundColor

                          (if
                            (om/get-state owner :debug-highlight)
                            (do
                              (if (not= (:mode @debugger-ui) "component")
                                (set-debug-component  react-fn-name   (om/get-state owner :parent-path)))
                              "lightGray")
                            (do
                              (if (not= (:mode @debugger-ui) "component")
                                (unset-debug-component  react-fn-name  (om/get-state owner :parent-path)))
                              "")
                            ""
                            )
                          })
            }
       (react-fn data)
       ""))))




(defn attrs [attributes]
  (if attributes
    (if (:style attributes)
      (clj->js (merge attributes {:style (clj->js (:style attributes))}))
      (clj->js attributes)
      )
    nil)
  )















(defn when-path-equals [watcher path value fn-def]
  (swap! watcher conj
         {
          :type     "path equals"
          :path     path
          :value    value
          :fn       fn-def
          }))





(defn when-value-changes [watcher path fn-def]
  (swap! watcher conj
         {
          :type     "value change"
          :path     path
          :fn       fn-def
          }))







(defn amend-record [records field value amend-fn]
  (into [] (map
            (fn[x] (if (= (get x field) value) (amend-fn x) x))
            records )))







(defn when-property-equals-in-record  [watcher path field value fn-def]
 (swap! watcher conj
         {
          :type     "record property equals"
          :path     path
          :field    field
          :value    value
          :fn       fn-def
          }))







(defn when-ui-path-equals-fn
  [path value ui-fn]

  (when-path-equals
   ui-watchers
   path
   value
   ui-fn))






(defn when-ui-value-changes-fn
  [path ui-fn]

  (when-value-changes
   ui-watchers
   path
   ui-fn))






(defn when-ui-property-equals-in-record
  [path field value ui-fn]

  (when-property-equals-in-record
   ui-watchers
   path
   field
   value
   ui-fn))







(defn when-data-path-equals-fn
  [path value data-fn]

  (when-path-equals
   data-watchers
   path
   value
   data-fn))






(defn when-data-value-changes-fn
  [path data-fn]

  (when-value-changes
   data-watchers
   path
   data-fn))






(defn when-data-property-equals-in-record
  [path field value data-fn]

  (when-property-equals-in-record
   data-watchers
   path
   field
   value
   data-fn))






(defn component-fn [coils-fn state parent-path rel-path]
  (do
    ;(log (str "component: " rel-path))
    (om/build
     coils-fn
     (get-in state rel-path)
     {:init-state {
                   :parent-path               (into [] (flatten (conj parent-path rel-path)))
                   }}
     )))





(defn add-refresh-path  [str-nm  path]
  (if (get @paths-for-refresh (str str-nm))

    (reset! paths-for-refresh
            (assoc @paths-for-refresh (str str-nm)
                      (conj (get @paths-for-refresh (str str-nm))
                            path)))

    (reset! paths-for-refresh
            (assoc @paths-for-refresh (str str-nm) #{path}))
    )
)

@paths-for-refresh



(defn record-component-call [caller-namespace-name
                             called-fn-name
                             state
                             full-path]

  (let [
        entry-name    (str caller-namespace-name ": " full-path)
        is-diff?      (not (= (pr-str state) (last (get @gui-calls  entry-name) )))
        debug-id      (add-debug-event   :event-type      "render"
                                         :component-name  called-fn-name
                                         :component-path  full-path
                                         :component-data  state)
        ]
    (do
      (add-refresh-path  called-fn-name  full-path)
      (reset! gui-calls (assoc @gui-calls entry-name


                          (if is-diff?
                            (conj
                             (if (get @gui-calls entry-name) (get @gui-calls  entry-name) [])   (pr-str state))
                            (get @gui-calls  entry-name))))
      (log (str "DEBUG ID: "debug-id))
      debug-id
      )))




;(keys @gui-calls)

;(get @gui-calls "splash-screen: []:[:ui :splash-screen]")

;@gui-calls

;clojure.zip/down


(defn write-ui-fn [tree  path  sub-path  value  parent-id]
  (let [
        full-path          (into [] (flatten (conj path sub-path)))
        old-val            @app-state
        data-access-key    {:tree  "UI"
                            :path  full-path}
        current-value      (get @data-accesses  data-access-key)
        ]
    (om/update!  tree  sub-path  value)
    (let [debug-id       (add-debug-event
                          :event-type  "UI"
                          :old         old-val
                          :new         @app-state
                          :parent-id   parent-id
                          )]
      (log (str full-path))
      (log (str "    parent id: " parent-id))
      (reset!  data-accesses (assoc @data-accesses
                               data-access-key
                               (if current-value
                                 (conj current-value  debug-id)
                                 [debug-id])))

      (remove-debug-event debug-id)
      )))


(defn write-data-fn [tree  path  value  parent-id]
  (let [
        full-path          path
        old-val            @ data-state
        data-access-key    {:tree  "DATA"
                            :path  full-path}
        current-value      (get @data-accesses  data-access-key)
        ]
    (om/update!
     tree
     path  value)
    (let [debug-id       (add-debug-event
                          :event-type  "DATA"
                          :old         old-val
                          :new         @app-state
                          :parent-id   parent-id
                          )]
      (reset!  data-accesses (assoc @data-accesses
                               data-access-key
                               (if current-value
                                 (conj current-value  debug-id)
                                 [debug-id])))

      (remove-debug-event debug-id)
      )))



(defn read-ui-fn [tree  path  sub-path  parent-id]
  (let [
        full-path          (into [] (flatten (conj path sub-path)))
        value              (get-in  tree  sub-path)
        data-access-key    {:tree  "UI"
                            :path  full-path}
        current-value      (get @ data-accesses  data-access-key)
        debug-id           (last @ call-stack)
        ]
    (log (str "*read-ui-fn: " full-path "    parent id: " debug-id))
    (reset!  data-accesses (assoc @data-accesses
                             data-access-key
                             (if current-value
                               (conj current-value  debug-id)
                               [debug-id])))

    ;(remove-debug-event  debug-id)
    value))




(defn get-in-tree
  "
  "
  [app path]
  (let
    [
     calls          @call-stack
     parent-id      (last calls)
     ]
  (read-ui-fn   app   []  path   parent-id)
  ))

@ data-accesses



(defn update-ui [app  path  value]
  (let
    [
     calls          @call-stack
     parent-id      (last calls)
     ]
  (write-ui-fn  app  [] path value parent-id)))


(defn add-many-fn [items]
  (apply
   om.dom/div nil items))








(cljs.reader/register-tag-parser! "webapp.framework.server.records.NeoNode" map->NeoNode)


;----------------------------------------------------------
(defn neo4j-fn
  "Call the server side neo4j function"
  ([cypher-str]
  ;----------------------------------------------------------
  (go
   (<! (remote-fn
        "!neo4j"
        {
         :cypher    cypher-str
         :params    {}}))))
  ([cypher-str params]
  ;----------------------------------------------------------
  (go
   (<! (remote-fn
        "!neo4j"
        {
         :cypher    cypher-str
         :params    params}))))

([cypher-str   params   return]
  ;----------------------------------------------------------
  (go
   (<! (remote-fn
        "!neo4j_nodes"
        {
         :cypher    cypher-str
         :params    params
         :return    return})))))









;----------------------------------------------------------
(defn add-to-simple-point-layer
  [item  layer-name]
;----------------------------------------------------------
    (go
        (<! (remote-fn
             "!add-to-simple-point-layer"
             {:node         item
              :layer-name   layer-name}))))






;----------------------------------------------------------
(defn find-names-within-distance
  [layer-name  x  y  km]
  ;----------------------------------------------------------
    (go
        (<! (remote-fn
             "!find-names-within-distance"
             {:layer-name layer-name
              :x          x
              :y          y
              :dist-km    km}))))




;----------------------------------------------------------
(defn find-names-within-bounds
  [layer-name  min-x  min-y  max-x  max-y]
  ;----------------------------------------------------------
  (go
   (<! (remote-fn "!find-names-within-bounds"   {:layer-name layer-name
                                              :min-x min-x
                                              :min-y min-y
                                              :max-x max-x
                                              :max-y max-y
                                              }))))


