(ns webapp.framework.client.coreclient
  (:require
    [goog.net.XhrIo          :as xhr]
    [clojure.browser.repl    :as repl]
    [cljs.reader             :as reader]
    [goog.dom]
    [goog.Uri.QueryData]
    [goog.events]
    [om.core          :as om :include-macros true]
    [om.dom           :as dom]
    [cljs.core.async :as async :refer [chan close!]]
  )
  (:require-macros
    [cljs.core.async.macros :refer [go alt!]])
  (:use
    [clojure.browser.event :only [listen]]
    [webapp.framework.client.system-globals  :only  [touch
                                                     debugger-ui
                                                     ]]
  )
)





(def auto-gen-id (atom 0))

(defn new-dom-id []
  (swap! auto-gen-id inc)
  (str "autodom" @auto-gen-id)
  )

(def gui-html (atom {}))
(def el-fn-mapping (atom {}))
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
  ;(.log js/console (str s))
  nil
)


(defn send-request2 [ address ]
      (let
      [
        ch            (chan 1)
        headers       (goog.structs.Map.)
        io-object     (goog.net.XhrIo.)
      ]
      (goog.events.listen
          io-object
          goog.net.EventType.COMPLETE
          (fn [ event ]
                  (let
                            [
                              target          (.-target event)
                              status          (. target (getStatus))
                            ]
                                  (if (= status 200)
                                    (let
                                    [
                                      response-text   (. target (getResponseText))
                                    ]
                                      (go
                                        (>! ch (reader/read-string response-text))
                                        (close! ch)
                                      ))

                                       (go
                                           (>! ch  {:error "true"})
                                           (close! ch)
                                       )
                                    )
                    )
               )
            )
            (. headers set "charset" "UTF-8")
            (. io-object send address "POST" nil headers)
            ch
          ))







(defn remote
([
  action
  ]
     (remote action {}))
([
  action
  parameters-in
  ]
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
               (str
                 (encode-parameter
                   x
                   (get parameters x)) "&" ) )
                 (keys parameters))))



                        )))
  )










(defn sql-fn [sql-str params]
  (go
    (<! (remote
                "!sql" {:sql sql-str :params params}))
  )
)

(defn neo4j-fn [cypher-str params]
  (go
    (<! (remote
                "!neo4j" {:cypher cypher-str :params params}))
  )
)

(go
 (let [env (:value (<! (remote "!get-environment" {})))]
   (if (= env "dev")
     (reset! debug-mode true)
     )))











(defn GET [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go
                      (>! ch res)
                      (close! ch)))))
    ch))







(defn from-server []
  (GET "http://127.0.0.1:3000/main.html")
)





(comment defn debug [html2 fname]


    (let [
            html            (make-el html2)
            current-id      (attr ($ html) "id")
            id              (if current-id
                              current-id
                              (let [new-id (new-dom-id)]
                                (attr  ($ html) "id" new-id)
                                new-id
                              )
                            )
          ]

      (attr  ($ html) "onmouseover"
             (str "webapp.framework.client.coreclient.clicked2('" id  "');")
             )
      (attr  ($ html) "onclick"
             (str "webapp.framework.client.coreclient.showcodepopover('" id  "');")
             )
      (attr  ($ html) "onmouseout"
             (str "webapp.framework.client.coreclient.clicked3('" id  "');")
             )
      ;(.log js/console (str "ID: " id))
      ;(.log js/console (str "fname: " fname))
      (swap! el-fn-mapping assoc id fname)

      html)


)






(defn- xml-str
 "Like clojure.core/str but escapes < > and &."
 [x]
  (-> x str
      ;(clojure.string/replace #" " "  " )
      (clojure.string/replace #"&amp;" "&" )
      (clojure.string/replace #"&lt;" "<")
      (clojure.string/replace #"&gt;" ">" )))







(comment
  (record-defn-ui-component
   "a.b" "start" '[a b] '(def 1)))

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
                     fname " "
                     args (char 13) (char 13)
                     code-str
                     ""
                     )))
     )
    )
  )










(defn process-ui-component [
                            {absolute-path :absolute-path}
                            ]
  (if absolute-path
    (do
      (touch  absolute-path)))

  )




(defn  ^:export loadDebugger []
  (doall
    (om/root
     webapp.framework.client.components.debugger-main/main-debug-comp
     debugger-ui
     {:target (js/document.getElementById "right_of_main")})

    (om/root
     webapp.framework.client.components.debugger-main/main-debug-slider-comp
     debugger-ui
     {:target (js/document.getElementById "main_playback_slider")})


    ))


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
             [:mode] "browse"))


    (om/root
     (fn [app owner] (om/component (dom/div nil "")))
     debugger-ui
     {:target (js/document.getElementById "right_of_main")})


    ))

;(unloadDebugger)


(defn display-debug-code []
  (let [component-name  (last (get @debugger-ui :react-components))]
    (reset! debugger-ui
            (assoc-in @debugger-ui [:current-component]
                      component-name))
    ))




(defn component-clicked [x]
  (do
    (reset! debugger-ui
            (assoc-in @debugger-ui [:mode]
                      "component"))
    (reset! debugger-ui
            (assoc-in @debugger-ui [:current-component]
                      (last (get @debugger-ui :react-components))))
    ))


(defn set-debug-component [component-name]
  (do
    (if (not-any? #(= %1 component-name) (get @debugger-ui :react-components))
      (reset! debugger-ui
              (assoc-in @debugger-ui [:react-components]
                        (conj (get @debugger-ui :react-components)
                              component-name
                              )))
      (display-debug-code)
      )))



(defn unset-debug-component [component-name]
  (do
    (reset! debugger-ui

            (assoc-in
             @debugger-ui
             [:react-components]
             (into []
                   (filter #(not= %1 component-name)
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
    (dom/div (if js/debug_live
               #js {
                  :onMouseEnter #(om/set-state! owner :debug-highlight true)
                  :onMouseLeave #(om/set-state! owner :debug-highlight false)
                  :onClick component-clicked
                  :style #js {:backgroundColor

                                (if
                                  (om/get-state owner :debug-highlight)
                                  (do
                                    (if (= (:mode @debugger-ui) "browse")
                                      (set-debug-component  react-fn-name))
                                    "lightGray")
                                  (do
                                    (if (= (:mode @debugger-ui) "browse")
                                    (unset-debug-component  react-fn-name))
                                    "")
                                  ""
                                  )
                              }
                  } )

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





