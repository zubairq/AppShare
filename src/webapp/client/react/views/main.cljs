(ns webapp.client.react.views.main
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [webapp.framework.client.coreclient])

  (:use
   [webapp.client.ui-helpers                :only  [validate-email
                                                    validate-full-name
                                                    validate-endorsement
                                                    blur-field
                                                    update-field-value
                                                    basic-input-box ]]
   [clojure.string :only [blank?]]
   [webapp.client.react.components.forms               :only  [request-form]]
   [webapp.client.react.components.login               :only  [login]]
   [webapp.client.react.components.connection-graph    :only  [graph text-graph latest-endorsements]]
   [webapp.client.react.components.company-details     :only  [company-details2]]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils]]))
(ns-coils 'webapp.client.react.views.main)







(defn select-browser [e app]
  (om/update! app [:ui :tab]  "browser"))

(defn select-request [e app]
  (om/update! app [:ui :tab]  "request"))

(defn select-login [e app]
  (om/update! app [:ui :tab]  "login"))



;------------------------------------------------------------
(defn-ui-component     browser-menu   [app]
  {:absolute-path []}
  ;------------------------------------------------------------

  (dom/div  #js {:style #js {:paddingBottom "10px"}}


           (dom/div
            (if  (= (-> app :ui :tab-browser) "top companies")
              #js {:style #js {
                               	:display "inline-block"}
                   }

            #js {:className  ""
                   :onClick        (fn[e] (om/update! app [:ui :tab-browser]  "top companies"))
                   :onTouchStart   (fn[e] (select-browser e app))
                   :style #js {:textDecoration "underline"
                               	:display "inline-block"}
                   })
            "Top")


           (dom/div
              #js {:style #js {
                               :display "inline-block"
                               :width "20px"}
                   } "")


           (dom/div
            (if  (= (-> app :ui :tab-browser) "latest endorsements")
              #js {:style #js {
                               	:display "inline-block"}
                   }
              #js {:className  ""
                   :onClick        (fn[e] (om/update! app [:ui :tab-browser]  "latest endorsements"))
                   :onTouchStart   (fn[e] (select-browser e app))
                   :style #js {:textDecoration "underline"
                               :display "inline-block"}
                   })
            "latest")

))




;------------------------------------------------------------
(defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (dom/div nil
           (dom/h2 nil "ConnectToUs.co")



           (dom/ul
            #js {:className  "nav nav-tabs"}

            (dom/li #js {:className  (if (= (-> app :ui :tab) "browser") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (select-browser e app))
                                :onTouchStart   (fn[e] (select-browser e app))

                                } "Search"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "request") "active" "") }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (select-request e app))
                                :onTouchStart   (fn[e] (select-request e app))


                                } "Add"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "login") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (select-login e app))
                                :onTouchStart   (fn[e] (select-login e app))

                                } "Login"))

            )


           (cond
            (= (-> app :ui :tab) "browser")
            (om/build  browser-menu  (-> app ))
           )

           (cond
            (= (-> app :ui :tab) "request")
            (om/build  request-form  (-> app :ui :request))


            (= (-> app :ui :tab) "login")
            (om/build  login  (-> app :ui :login))

            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "top companies"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (om/build  text-graph    (-> app :ui :companies))


             (= (-> app :ui :graph-ab-test) "SVG")
             (om/build  graph
                        {
                         :data    (:data    app)
                         }))


            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "latest endorsements"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (om/build  latest-endorsements
                        (-> app :ui :latest-endorsements))


             (= (-> app :ui :graph-ab-test) "SVG")
             (om/build  graph
                        {
                         :data    (:data    app)
                         }))


           (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "company"))

             (om/build  company-details2   (-> app :ui :company-details))


)))
