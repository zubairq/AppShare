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

       (webapp.framework.client.coreclient/record-defn-ui-component
             (~'ns-coils-debug)
             ~(str `~fn-name) ~(str `~data-paramater-name)

          (str ~(with-out-str   (write (first `~code))
                                        :dispatch clojure.pprint/code-dispatch))
        )

       (defn ~fn-name [~(first data-paramater-name)  ~'owner]
         (~'reify

          ~'om/IInitState
          (~'init-state ~'[_]
                      {:debug-highlight false})


           ~'om/IRender
          (~'render
           [~'this]

           ~(if *show-code*
              `(~'let [~'path ~'(om/get-state owner :parent-path)]
                      (webapp.framework.client.coreclient/debug-react
                       ~(str `~fn-name)
                       ~'owner
                       ~(first data-paramater-name)
                       (~'fn [~(first data-paramater-name)]
                             ~@code)))

              ;else
              (first code))
           )
         ))


       (~'webapp.framework.client.coreclient/process-ui-component  ~opts)

       ))


(macroexpand
  '(defn-ui-component abc [data] {:path [:ui :request] }
     (dom/div  nil  " You asdsddsads")
      ))
;--------------------------------------------------------------------












;--------------------------------------------------------------------
(defmacro div [attributes & more]
  `(dom/div  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro a [attributes & more]
  `(dom/a  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro h2 [attributes & more]
  `(dom/h2  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
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

     (~'when-ui-value-changes-fn
      ~path
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------





;--------------------------------------------------------------------
(defmacro ==ui
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

     (~'when-ui-path-equals-fn
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

     (~'when-data-value-changes-fn
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

     (~'when-data-path-equals-fn
      ~path
      ~value
      (~'fn [~'ui] (do ~@code)))))
;--------------------------------------------------------------------







;--------------------------------------------------------------------
(defmacro ui-tree
  [path]
  `(~'get-in-tree ~'ui ~path))

(defmacro <--ui
  [path]
  `(~'get-in-tree ~'ui ~path))

(comment macroexpand
 '(==ui  [:ui   :company-details   :clicked]    true

      (-->ui  [:ui  :company-details   :clicked  ] false)
      (-->ui  [:ui  :tab-browser    ] "top companies")))



(defmacro ui-tree!
  [path value]
  `(~'update-ui ~'ui ~path ~value))

(defmacro -->ui
  [path value]
  `(~'update-ui ~'ui ~path ~value))

;(macroexpand '(ui-tree! [:ui :request :from-email :error] ""))




(comment macroexpand '(when-ui-value-changes [:ui :company-details :company-url]


   (go
    (update-ui  ui  [:ui  :company-details   :skills  ] nil)
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
  `(do
     (webapp.framework.client.coreclient/record-component-call
      (~'ns-coils-debug)
      ~(str `~component-render-fn)
      ~state
      ~'path
      ~rel-path
      )
    (~'component-fn ~component-render-fn ~state  ~'path ~rel-path)))

;(macroexpand '(component  main-view  app []) )
;(macroexpand '(defn-ui-component    letter-a  [data] {}    (div nil "a")))
;--------------------------------------------------------------------
