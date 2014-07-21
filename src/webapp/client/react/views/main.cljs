(ns webapp.client.react.views.main
  (:require
   [webapp.framework.client.coreclient :as coils]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom]
   [clojure.data     :as data]
   [clojure.string   :as string])

  (:use
   [clojure.string :only [blank?]]
   [webapp.client.react.components.forms               :only  [request-form]]
   [webapp.client.react.components.login               :only  [login]]
   [webapp.client.react.components.connection-graph    :only  [graph text-graph latest-endorsements]]
   [webapp.client.react.components.company-details     :only  [company-details2]]
   [webapp.framework.client.coreclient     :only  [log remote component-fn]]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils div component]]))

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

  (div  {:style {:paddingBottom "10px"}}


            (div
             (cond
              (= (-> app :ui :tab-browser) "top companies")
              {:style {:display "inline-block"}}

              :else
              {:className  ""
               :onClick        (fn[e] (om/update! app [:ui :tab-browser]  "top companies"))
               :onTouchStart   (fn[e] (select-browser e app))
               :style {:textDecoration "underline"
                       :display "inline-block"}
               })
             "Top")


           (div
              {:style {
                               :display "inline-block"
                               :width "20px"}
                   } "")


           (div
            (if  (= (-> app :ui :tab-browser) "latest endorsements")
              {:style  {
                               	:display "inline-block"}
                   }
              {:className  ""
                   :onClick        (fn[e] (om/update! app [:ui :tab-browser]  "latest endorsements"))
                   :onTouchStart   (fn[e] (select-browser e app))
                   :style {:textDecoration "underline"
                               :display "inline-block"}
                   })
            "latest")

))

;------------------------------------------------------------
(defn-ui-component    letter-a  [data]
  {}
  ;------------------------------------------------------------
    (div nil "a"))



;------------------------------------------------------------
(defn-ui-component    splash-screen  [splash-screen-data]
  {}
  ;------------------------------------------------------------
  (if (get-in splash-screen-data [:show])
    (div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "90%"
                  :height  "90%"
                  :border            "solid 1px black;"   :zIndex  "2000"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(om/update! splash-screen-data [:show] false)
          :onClick      #(om/update! splash-screen-data [:show] false)
          }

         (component   letter-a    splash-screen-data  [] )
         (div {:style { :vertical-align "center" }}

              (div {:style {:padding "5px" :padding-bottom "0px" :font-size "40"}} (str "ConnectToUs"))

              (div {:style {:padding "5px" :padding-bottom "20px"  :font-size "25"}}
                   (str "Sell more by showing your customer connections"))

                   (div {:style {:padding "5px" :padding-bottom "20px"}} "Click to start...")
              ))))





;------------------------------------------------------------
(defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (div {:style {:height "100%"}}

       (dom/h2 nil "ConnectToUs.co")

       (component  letter-a    app  [] )

       (component  splash-screen  app  [:ui :splash-screen])



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


                                } "Connect"))

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
