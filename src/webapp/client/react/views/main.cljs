(ns webapp.client.react.views.main
  (:require
   [om.core                              :as om :include-macros true]
   [webapp.framework.client.coreclient   :as coils :include-macros true]
   [om.dom                               :as dom])

  (:use
   [webapp.client.react.components.forms               :only  [request-form]]
   [webapp.client.react.components.login               :only  [login]]
   [webapp.client.react.components.connection-graph    :only  [graph
                                                               text-graph
                                                               latest-endorsements]]

   [webapp.client.react.components.company-details     :only  [company-details2]]
))

(coils/ns-coils 'webapp.client.react.views.main)









;------------------------------------------------------------
(coils/defn-ui-component     browser-menu   [app]
  {:absolute-path []}
  ;------------------------------------------------------------

  (coils/div  {:style {:paddingBottom "10px"}}


            (coils/div
             (cond
              (= (-> app :ui :tab-browser) "top companies")
              {:style {:display "inline-block"}}

              :else
              {:className  ""
               :onClick        (fn[e] (coils/write-ui  app  [:ui :tab-browser]  "top companies"))
               :onTouchStart   (fn[e] (coils/write-ui app [:ui :tab-browser]  "top companies"))
               :style {:textDecoration "underline"
                       :display "inline-block"}
               })
             "Top")


           (coils/div
              {:style {
                               :display "inline-block"
                               :width "20px"}
                   } "")


           (coils/div
            (if  (= (-> app :ui :tab-browser) "latest endorsements")
              {:style  {
                               	:display "inline-block"}
                   }
              {:className  ""
                   :onClick        (fn[e] (coils/write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :onTouchStart   (fn[e] (coils/write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :style {:textDecoration "underline"
                               :display "inline-block"}
                   })
            "latest")

))





;------------------------------------------------------------
(coils/defn-ui-component    letter-a  [data]
  {}
  ;------------------------------------------------------------
    (coils/div nil "-----------------------------------------------"))








;------------------------------------------------------------
(coils/defn-ui-component    splash-screen  [splash-screen-data]
  {}
  ;------------------------------------------------------------
  (if (get-in splash-screen-data [:show])
    (coils/div {:style {:position          "relative" :width   "0" :height  "0"
                  }}
    (coils/div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "400"
                  :height  "400"
                  :border            "solid 1px black;"   :zIndex  "2200"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(coils/write-ui splash-screen-data [:click] true)
          :onClick      #(coils/write-ui splash-screen-data [:click] true)
          }

         (coils/div {:style { :vertical-align "center" :width "100%" :height "100%"}}

              (coils/div {:style {:padding "5px" :padding-bottom "0px" :font-size "40"}} (str "Coils"))

              (coils/div {:style {:padding "5px" :padding-bottom "20px"  :font-size "25"}}
                   (str "Build Neo4j Web Apps"))

              (coils/a {:href "https://github.com/zubairq/coils"}
                 "Follow us on GitHub")

              (coils/component   letter-a   splash-screen-data [])



              (coils/div {:style {:padding "5px" :padding-bottom "20px"  :font-size "20"}}
                   (str "This is a basic demo app called ConnectToUs to show the basic capabilities of Coils"))



                   (coils/div {:style {:padding "5px" :padding-bottom "20px"}} "Click to start...")
              )))))







(coils/==ui  [:ui :splash-screen :click]   true

    (do
      (coils/-->ui [:ui  :splash-screen  :click]  false)
      (coils/-->ui [:ui  :splash-screen  :show]   false)))




;------------------------------------------------------------
(coils/defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (coils/div {:style {
                :height       (- (-> app :view :height) 100)
                :zIndex       "1"
                }}

       (dom/h2 nil "ConnectToUs.co")

       (coils/component  splash-screen  app  [:ui :splash-screen])

         (coils/component letter-a  app [:ui])


           (dom/ul
            #js {:className  "nav nav-tabs"}

            (dom/li #js {:className  (if (= (-> app :ui :tab) "browser") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (coils/write-ui app [:ui :tab]  "browser"))
                                :onTouchStart   (fn[e] (coils/write-ui app [:ui :tab]  "browser"))

                                } "Search"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "request") "active" "") }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (coils/write-ui app [:ui :tab]  "request"))
                                :onTouchStart   (fn[e] (coils/write-ui app [:ui :tab]  "request"))


                                } "Connect"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "login") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (coils/write-ui app [:ui :tab]  "login"))
                                :onTouchStart   (fn[e] (coils/write-ui app [:ui :tab]  "login"))

                                } "Login"))

            )


           (cond
            (= (-> app :ui :tab) "browser")
            (coils/component  browser-menu  app [])
           )

           (cond
            (= (-> app :ui :tab) "request")
            (coils/component  request-form  app [:ui :request] )


            (= (-> app :ui :tab) "login")
            (coils/component  login  app    [:ui :login])

            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "top companies"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (coils/component  text-graph    app [:ui :companies])


             (= (-> app :ui :graph-ab-test) "SVG")
             (coils/component  graph app
                        [:data  :data ]))


            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "latest endorsements"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (coils/component  latest-endorsements
                        app [:ui :latest-endorsements])


             (= (-> app :ui :graph-ab-test) "SVG")
             (coils/component  graph app
                        [:data  :data ]))


           (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "company"))

             (coils/component  company-details2   app [:ui :company-details])


)))
