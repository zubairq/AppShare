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



(defmacro log [& x]
  `(.log js/console (str
                     ~@ x)))


;(macroexpand '(log "a" "b"))
;--------------------------------------------------------------------








;--------------------------------------------------------------------
(defmacro sql [sql-str params]
  `(webapp.framework.client.coreclient.sql-fn
       ~(encrypt sql-str)
       ~params))

;( macroexpand '(sql "SELECT * FROM test_table where name = ?" ["shopping"] ))
;--------------------------------------------------------------------








;--------------------------------------------------------------------
(defmacro ns-coils [namespace-name]
  `(defn ~'ns-coils-debug  [] (str ~namespace-name)))


;(macroexpand '(ns-coils dfd))
;--------------------------------------------------------------------










;--------------------------------------------------------------------
(defmacro defn-ui-component [fn-name data-paramater-name opts & code ]

    `(do

       (defn ~fn-name [~(first data-paramater-name)  ~'owner]
         (~'reify

          ~'om.core/IInitState
          (~'init-state ~'[_]
                      {:debug-highlight false})


           ~'om.core/IRender
          (~'render
           [~'this]

           ~(if *show-code*
              `(~'let [

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
                                           ~@code))

                       ~'removed-id     (~'webapp.framework.client.coreclient/remove-debug-event  ~'debug-id)

                       ]

                      ~'return-val)

              ;else
              (first code))
           )
         ))


       (webapp.framework.client.coreclient/record-defn-ui-component
             (~'ns-coils-debug)
             ~fn-name
             ~(str `~fn-name) ~(str `~data-paramater-name)

          (str ~(with-out-str   (write (first `~code))
                                        :dispatch clojure.pprint/code-dispatch)))




       (~'webapp.framework.client.coreclient/process-ui-component  ~opts)

       ))


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
(defmacro h2 [attributes & more]
  `(om.dom/h2  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
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




(comment macroexpand '(when-ui-value-changes [:ui :company-details :company-url]


   (go
    (webapp.framework.client.coreclient/update-ui  ui  [:ui  :company-details   :skills  ] nil)
     (let [ l (<! (remote "get-company-details"
             {
              :company-url    (get-in @app-state [:ui :company-details :company-url])
              }))]

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
