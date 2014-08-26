(ns webapp.client.react.views.main
  (:require
   [om.core                              :as om :include-macros true]
   [webapp.framework.client.coreclient   :as c :include-macros true]
   [om.dom                               :as dom])

  (:use
   [webapp.client.react.components.forms               :only  [request-form]]
   [webapp.client.react.components.login               :only  [login]]
   [webapp.client.react.components.connection-graph    :only  [graph
                                                               text-graph
                                                               latest-endorsements]]

   [webapp.client.react.components.company-details     :only  [company-details2]]
))

(c/ns-coils 'webapp.client.react.views.main)









;------------------------------------------------------------
(c/defn-ui-component     browser-menu   [app]
  {:absolute-path []}
  ;------------------------------------------------------------

  (c/div  {:style {:paddingBottom "10px"}}


            (c/div
             (cond
              (= (-> app :ui :tab-browser) "top companies")
              {:style {:display "inline-block"}}

              :else
              {:className  ""
               :onClick        (fn[e] (c/write-ui  app  [:ui :tab-browser]  "top companies"))
               :onTouchStart   (fn[e] (c/write-ui app [:ui :tab-browser]  "top companies"))
               :style {:textDecoration "underline"
                       :display "inline-block"}
               })
             "Top")


           (c/div
              {:style {
                               :display "inline-block"
                               :width "20px"}
                   } "")


           (c/div
            (if  (= (-> app :ui :tab-browser) "latest endorsements")
              {:style  {
                               	:display "inline-block"}
                   }
              {:className  ""
                   :onClick        (fn[e] (c/write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :onTouchStart   (fn[e] (c/write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :style {:textDecoration "underline"
                               :display "inline-block"}
                   })
            "latest")

))





;------------------------------------------------------------
(c/defn-ui-component    letter-a  [data]
  {}
  ;------------------------------------------------------------
    (c/div nil "-----------------------------------------------"))








;------------------------------------------------------------
(c/defn-ui-component    splash-screen  [splash-screen-data]
  {}
  ;------------------------------------------------------------
  (if (get-in splash-screen-data [:show])
    (c/div {:style {:position          "relative" :width   "0" :height  "0"
                  }}
    (c/div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "400"
                  :height  "400"
                  :border            "solid 1px black;"   :zIndex  "2200"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(c/write-ui splash-screen-data [:click] true)
          :onClick      #(c/write-ui splash-screen-data [:click] true)
          }

         (c/div {:style { :vertical-align "center" :width "100%" :height "100%"}}

              (c/div {:style {:padding "5px" :padding-bottom "0px" :font-size "40"}} (str "Coils"))

              (c/div {:style {:padding "5px" :padding-bottom "20px"  :font-size "25"}}
                   (str "Build Neo4j Web Apps"))

              (c/a {:href "https://github.com/zubairq/coils"}
                 "Follow us on GitHub")

              (c/component   letter-a   splash-screen-data [])



              (c/div {:style {:padding "5px" :padding-bottom "20px"  :font-size "20"}}
                   (str "This is a basic demo app called ConnectToUs to show the basic capabilities of Coils"))



                   (c/div {:style {:padding "5px" :padding-bottom "20px"}} "Click to start...")
              )))))







(c/==ui  [:ui :splash-screen :click]   true

    (do
      (c/-->ui [:ui  :splash-screen  :click]  false)
      (c/-->ui [:ui  :splash-screen  :show]   false)))




;------------------------------------------------------------
(c/defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (c/div {:style {
                :height       (- (-> app :view :height) 100)
                :zIndex       "1"
                }}

       (dom/h2 nil "ConnectToUs.co")

       (c/component  splash-screen  app  [:ui :splash-screen])

         (c/component letter-a  app [:ui])


           (dom/ul
            #js {:className  "nav nav-tabs"}

            (dom/li #js {:className  (if (= (-> app :ui :tab) "browser") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (c/write-ui app [:ui :tab]  "browser"))
                                :onTouchStart   (fn[e] (c/write-ui app [:ui :tab]  "browser"))

                                } "Search"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "request") "active" "") }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (c/write-ui app [:ui :tab]  "request"))
                                :onTouchStart   (fn[e] (c/write-ui app [:ui :tab]  "request"))


                                } "Connect"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "login") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (c/write-ui app [:ui :tab]  "login"))
                                :onTouchStart   (fn[e] (c/write-ui app [:ui :tab]  "login"))

                                } "Login"))

            )


           (cond
            (= (-> app :ui :tab) "browser")
            (c/component  browser-menu  app [])
           )

           (cond
            (= (-> app :ui :tab) "request")
            (c/component  request-form  app [:ui :request] )


            (= (-> app :ui :tab) "login")
            (c/component  login  app    [:ui :login])

            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "top companies"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (c/component  text-graph    app [:ui :companies])


             (= (-> app :ui :graph-ab-test) "SVG")
             (c/component  graph app
                        [:data  :data ]))


            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "latest endorsements"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (c/component  latest-endorsements
                        app [:ui :latest-endorsements])


             (= (-> app :ui :graph-ab-test) "SVG")
             (c/component  graph app
                        [:data  :data ]))


           (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "company"))

             (c/component  company-details2   app [:ui :company-details])


)))
