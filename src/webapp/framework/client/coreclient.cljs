(ns webapp.framework.client.coreclient
  (:require
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
   [clojure.browser.event :only [listen]]
   [webapp.framework.client.system-globals  :only  [touch
                                                    debugger-ui
                                                    record-pointer-locally
                                                    app-state
                                                    playback-app-state
                                                    playback-controls-state
                                                    reset-app-state
                                                    ui-watchers
                                                    call-stack
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    add-debug-event
                                                    remove-debug-event
                                                    component-usage
                                                    gui-calls
                                                    current-gui-path
                                                    app-watch-on?
                                                    data-accesses
                                                    ]]))









(def debug-mode (atom false))









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






(defn send-request2 [ address   action  parameters-in]
  (let
    [
     ch            (chan 1)
     headers       (goog.structs.Map.)
     io-object     (goog.net.XhrIo.)
     ]
    (goog.events.listen
     io-object
     goog.net.EventType.COMPLETE

     (fn [event]
       (let
         [target          (.-target event)
          status          (. target (getStatus))]
         (if (= status 200)
           (let [response-text   (. target (getResponseText))]
             (go
              (let [debug-id
                    (add-debug-event
                     :event-type  "remote"
                     :action-name (str action)
                     :input       parameters-in
                     :result      (reader/read-string response-text)
                     )]

                (>! ch (reader/read-string response-text))
                (close! ch)
                (remove-debug-event  debug-id)
                )))

           (go
            (let [debug-id
                  (add-debug-event
                   :event-type  "remote"
                   :action-name (str action)
                   :input       parameters-in
                   :result      (str "ERROR IN RESPONSE, HTTP : " status)
                   )]
              (>! ch  {:error "true"})
              (close! ch)
              (remove-debug-event  debug-id)
              ))



           ))))
    (. headers set "charset" "UTF-8")
    (. io-object send address "POST" nil headers)
    ch
    ))







(defn remote
  ([action]
   (remote action {}))



  ([action  parameters-in]
   (let
     [
      parameters  (if parameters-in
                    {:params parameters-in :tclock (get-time)})
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

      ))))










(defn sql-fn [sql-str params]
  (go
    (<! (remote
                "!sql" {:sql sql-str :params params}))))




(defn neo4j-fn [cypher-str params]
  (go
    (<! (remote
                "!neo4j" {:cypher cypher-str :params params}))))




(go
 (let [env (:value (<! (remote "!get-environment" {})))]
   (if (= env "dev")
     (reset! debug-mode true))))



(go
 (let [record-pointer-locally-value (:value (<! (remote "!get-record-pointer-locally" {})))]
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






(defn record-path= [namespace-name path value tree-name & code]
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


                "(==" tree-name " " path "  "
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





(defn record-defn-ui-component [namespace-name fname args & code]
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
                     ))))))


(comment
  (record-defn-ui-component
   "a.b" "start" '[a b] '(def 1)))









(defn process-ui-component [
                            {absolute-path :absolute-path}
                            ]
  (if absolute-path
    (do
      (touch  absolute-path))))








(defn  ^:export loadDebugger []
  (do
   (reset! app-watch-on? false)

    (om/root
     webapp.framework.client.components.debugger-main/main-debug-comp
     debugger-ui
     {:target (js/document.getElementById "right_of_main")})

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







(defn debug-react [str-nm owner data react-fn]
  (let
    [
     react-fn-name    (str str-nm)
     ]
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
       "")))






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








(defn record-component-call [caller-namespace-name
                             called-fn-name
                             state
                             full-path]

  (let [
        entry-name    (str called-fn-name ": " full-path)
        is-diff?      (not (= (pr-str state) (last (get @gui-calls  entry-name) )))
        debug-id      (add-debug-event :event-type      "render"
                                         :component-name  called-fn-name
                                         :component-path  full-path
                                         :component-data  state)
        ]
    (do
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
      )
    ))



(defn update-ui [app  path  value]
  (let
    [
     calls          @call-stack
     parent-id      (last calls)
     ]
  (write-ui-fn  app  [] path value parent-id)))
