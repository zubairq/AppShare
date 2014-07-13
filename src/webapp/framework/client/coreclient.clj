(ns webapp.framework.client.coreclient
  [:use [webapp.framework.server.encrypt]]
  (:use clojure.pprint)
  (:use webapp-config.settings)
  (:require [rewrite-clj.parser :as p])
  (:require [clojure.data :as di])
  (:require [rewrite-clj.printer :as prn])
  )


(defmacro log2 [& x]
  `(comment ~@ x))



(defmacro log [& x]
  `(.log js/console (str
                     ~@ x
                     ))
)

(macroexpand '(log "a" "b"))


(defmacro on-click [el & code]

    `(webapp.framework.client.coreclient/on-click-fn
             ~el
             (fn [] (do ~@ code))
    )
  )

(defmacro on-mouseover [el & code]

    `(webapp.framework.client.coreclient/on-mouseover-fn
             ~el
             (fn [] (do ~@code))
    )
  )


(comment macroexpand '(define-action
                   "clear main page2"
                   (clear "main")
))

(defmacro sql [sql-str params]
  `(webapp.framework.client.coreclient.sql-fn
       ~(encrypt sql-str)
       ~params
   )
)


;( macroexpand '(sql "SELECT * FROM test_table where name = ?" ["shopping"] ))





(defmacro ns-coils [namespace-name]
  `(defn ~'ns-coils-debug  [] (str ~namespace-name))
)


;(macroexpand '(ns-coils dfd))



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
               `(webapp.framework.client.coreclient/debug-react
                 ~(str `~fn-name)
                 ~'owner
                 ~(first data-paramater-name)
                 (~'fn [~(first data-paramater-name)]
                 ~@code))
                (first code))
         )))


       (~'webapp.framework.client.coreclient/process-ui-component  ~opts)

       ))


(macroexpand
  '(defn-ui-component abc [data] {:path [:ui :request] }
     (dom/div
      nil
      (dom/div nil (str " You asdsddsads" data))
      )))




(defmacro div [attributes & more]
  `(dom/div  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro a [attributes & more]
  `(dom/a  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))
(defmacro h2 [attributes & more]
  `(dom/h2  (webapp.framework.client.coreclient/attrs ~attributes) ~@more))


