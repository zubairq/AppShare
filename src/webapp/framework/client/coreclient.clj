(ns webapp.framework.client.coreclient
  [:use [webapp.framework.server.encrypt]]
  (:use clojure.pprint)
  (:use webapp-config.settings)
  (:require [rewrite-clj.parser :as p])
  (:require [clojure.data :as di])
  (:require [rewrite-clj.printer :as prn])
  )


;--------------------------------------------------------------------
; use this when you want to comment out logging, by removing the 2
(defmacro log2 [& x]
  `(comment ~@ x))



(defmacro remote

  ([action]
  `(~'<! (webapp.framework.client.coreclient/remote-fn  ~(str `~action))))

  ([action params]
  `(~'<! (webapp.framework.client.coreclient/remote-fn  ~(str `~action)  ~params)))
  )




(defmacro server-call [& x]
  `(cljs.core.async.macros/go ~@ x))

(macroexpand '(remote  a  {}))

(defmacro log [& x]
  `(.log js/console (str
                     ~@ x)))

(defmacro add-many [items]
  `(add-many-fn      ~items))

;(macroexpand '(log "a" "b"))
;--------------------------------------------------------------------













;--------------------------------------------------------------------
(defmacro ns-coils [namespace-name]
  `(defn ~'ns-coils-debug  [] (str ~namespace-name)))


;(macroexpand '(ns-coils dfd))
;--------------------------------------------------------------------








(defmacro aa [x]
  `(str ~(str `~x)))

(macroexpand '(aa "a1"))


;--------------------------------------------------------------------
(defmacro defn-ui-component
  ([fn-name  data-paramater-name  code ]
   `(defn-ui-component  ~fn-name  ~data-paramater-name  {}  ~code)
   )
  ([fn-name data-paramater-name opts code ]

    `(do

       (defn ~fn-name [~(first data-paramater-name)  ~'owner]
         (~'reify

          ~'om.core/IInitState
          (~'init-state ~'[_]
                      {:debug-highlight false})


           ~'om.core/IRender
          (~'render
           [~'this]

              (~'let [

                       ~'debug-id       (webapp.framework.client.coreclient/record-component-call
                                         (~'ns-coils-debug)
                                         ~(str `~fn-name)
                                         ~(first data-paramater-name)
                                         ~'(om.core/get-state owner :parent-path)
                                         )


                       ~'path       ~'(om.core/get-state owner :parent-path)

                       ~'parent-id  ~'debug-id

                       ~'return-val (webapp.framework.client.coreclient/debug-react
                                     ~(str `~fn-name)
                                     ~'owner
                                     ~(first data-paramater-name)
                                     (~'fn [~(first data-paramater-name)]
                                           ~code)
                                     ~'path
                                     ~'parent-id
                                     )

                       ~'removed-id     (~'webapp.framework.client.coreclient/remove-debug-event  ~'debug-id)

                       ]

                      ~'return-val)

           )
         ))


       (webapp.framework.client.coreclient/record-defn-ui-component
             (~'ns-coils-debug)
             ~fn-name
             ~(str `~fn-name) ~(str `~data-paramater-name)

          (str ~(with-out-str   (write `~code)
                                        :dispatch clojure.pprint/code-dispatch)))




       (webapp.framework.client.coreclient/process-ui-component  ~(str `~fn-name))

       )))


(macroexpand
  '(defn-ui-component abc [data] {:path [:ui :request] }
     (om.dom/div  nil  " You asdsddsads")
      ))
;--------------------------------------------------------------------












;--------------------------------------------------------------------
(defmacro div [attributes & more]
  `(om.dom/div  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro a [attributes & more]
  `(om.dom/a  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro h1 [attributes & more]
  `(om.dom/h1  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro h2 [attributes & more]
  `(om.dom/h2  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro h3 [attributes & more]
  `(om.dom/h3  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro ul [attributes & more]
  `(om.dom/ul  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro li [attributes & more]
  `(om.dom/li  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro pre [attributes & more]
  `(om.dom/pre  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro svg [attributes & more]
  `(om.dom/svg  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro circle [attributes & more]
  `(om.dom/circle  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro button [attributes & more]
  `(om.dom/button  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro input [attributes & more]
  `(om.dom/input  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro table [attributes & more]
  `(om.dom/table  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro tr [attributes & more]
  `(om.dom/tr  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro td [attributes & more]
  `(om.dom/td  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro th [attributes & more]
  `(om.dom/th  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro form [attributes & more]
  `(om.dom/form  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
;--------------------------------------------------------------------




;--------------------------------------------------------------------
(defmacro watch-ui
  [path & code]

  `(do

     (webapp.framework.client.coreclient/record-watcher
      (~'ns-coils-debug)
      ~(str `~path)
      "ui"
      (str ~(with-out-str   (write (first `~code))
              :dispatch clojure.pprint/code-dispatch))
      )

     (~'webapp.framework.client.coreclient/when-ui-value-changes-fn
      ~path
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------





;--------------------------------------------------------------------
(defmacro ==ui
  "Checks the UI tree for a value"
  [path value & code]

  `(do
     (webapp.framework.client.coreclient/record-path=
      (~'ns-coils-debug)
      ~(str `~path)
      ~(str `~value)
      "ui"
      (str ~(with-out-str   (write (first `~code))
              :dispatch clojure.pprint/code-dispatch))
      )

     (~'webapp.framework.client.coreclient/when-ui-path-equals-fn
      ~path
      ~value
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------







;--------------------------------------------------------------------
(defmacro watch-data
  [path & code]

  `(do
     (webapp.framework.client.coreclient/record-watcher
      (~'ns-coils-debug)
      ~(str `~path)
      "data"
      (str ~(with-out-str   (write (first `~code))
              :dispatch clojure.pprint/code-dispatch))
      )

     (~'webapp.framework.client.coreclient/when-data-value-changes-fn
      ~path
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------







;--------------------------------------------------------------------
(defmacro ==data
  [path value & code]

  `(do
     (webapp.framework.client.coreclient/record-path=
      (~'ns-coils-debug)
      ~(str `~path)
      ~(str `~value)
      "data"
      (str ~(with-out-str   (write (first `~code))
              :dispatch clojure.pprint/code-dispatch))
      )

     (~'webapp.framework.client.coreclient/when-data-path-equals-fn
      ~path
      ~value
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------







;--------------------------------------------------------------------
(defmacro <--ui
  [path]
  `(~'webapp.framework.client.coreclient/get-in-tree ~'ui ~path))

(comment macroexpand
 '(==ui  [:ui   :company-details   :clicked]    true

      (-->ui  [:ui  :company-details   :clicked  ] false)
      (-->ui  [:ui  :tab-browser    ] "top companies")))



(defmacro -->ui
  "Writes to the UI tree"
  [path value]
  `(~'webapp.framework.client.coreclient/update-ui ~'ui ~path ~value))

;(macroexpand '(-->ui [:ui :request :from-email :error] ""))






(defmacro  -->data
  "Writes to the data tree"
  [path value]
   `(~'webapp.framework.client.coreclient/-->data-fn ~path ~value))



(defmacro  <--data
  "Reads from the data tree"
  [path]
   `(~'webapp.framework.client.coreclient/<--data-fn ~path))











(comment macroexpand '(when-ui-value-changes [:ui :company-details :company-url]


   (go
    (webapp.framework.client.coreclient/update-ui  ui  [:ui  :company-details   :skills  ] nil)
     (let [ l (remote  get-company-details
             {
              :company-url    (get-in @app-state [:ui :company-details :company-url])
              })]

       ;(log (pr-str l))
       (update-data [:company-details]  l)
       ))))
;--------------------------------------------------------------------








;--------------------------------------------------------------------
(defmacro component
  [component-render-fn   state   rel-path]
  `(let [
         ~'return-value   (~'webapp.framework.client.coreclient/component-fn ~component-render-fn ~state  ~'path ~rel-path)
         ]
     (do
       ~'return-value)))

(macroexpand '(component  main-view  app []) )
;(macroexpand '(defn-ui-component    letter-a  [data] {}    (div nil "a")))
;--------------------------------------------------------------------



;--------------------------------------------------------------------
(defmacro write-ui
  [tree sub-path value]
  `(do
     (webapp.framework.client.coreclient/write-ui-fn
      ~tree
      ~'path
      ~sub-path
      ~value
      ~'parent-id
      )))



(defmacro read-ui
  [tree sub-path]
  `(do
     (webapp.framework.client.coreclient/read-ui-fn
      ~tree
      ~'path
      ~sub-path
      ~'parent-id
      )))

;(macroexpand '(read-ui app [:ui :tab-browser]))

;(macroexpand '(write-ui app [:ui :tab-browser]  "top companies"))



(defmacro neo4j
  ([cypher-str]
  `(~'<! (webapp.framework.client.coreclient/neo4j-fn
    ~(encrypt cypher-str)
    )))

  ([cypher-str params]
  `(~'<! (webapp.framework.client.coreclient/neo4j-fn
    ~(encrypt cypher-str)
    ~params
    )))

( [cypher-str params return]
  `(~'<! (webapp.framework.client.coreclient/neo4j-fn
    ~(encrypt cypher-str)
    ~params
    ~return
    ))))


(defmacro neo4j-1
  ([cypher-str]
  `(~'first (~'neo4j  ~cypher-str)))

  ([cypher-str  params]
  `(~'first (~'neo4j  ~cypher-str ~params)))

  ([cypher-str  params  return]
  `(~'first (~'neo4j  ~cypher-str ~params ~return)))
  )



;--------------------------------------------------------------------
(defmacro sql [sql-str params]
  `(~'<! (webapp.framework.client.coreclient/sql-fn
       ~(encrypt sql-str)
       ~params)))

;( macroexpand '(sql "SELECT * FROM test_table where name = ?" ["shopping"] ))
;--------------------------------------------------------------------

(defmacro sql-1 [sql-str params]
  `(~'first (~'sql  ~sql-str ~params)))




