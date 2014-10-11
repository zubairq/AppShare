(ns webapp.client.react.views.main
  (:require
   [webapp.framework.client.coreclient   :as c  :include-macros true])

  (:use
   [webapp.client.react.components.forms               :only  [request-form]]
   [webapp.client.react.components.login               :only  [login]]
   [webapp.client.react.components.connection-graph    :only  [graph
                                                               text-graph
                                                               latest-endorsements]]

   [webapp.client.react.components.company-details     :only  [company-details2]]
   )
  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils
                                               sql
                                               log
                                               neo4j
                                               defn-ui-component
                                               a
                                               div
                                               write-ui
                                               ]]
   )

  )

(ns-coils 'webapp.client.react.views.main)









;------------------------------------------------------------
(defn-ui-component     browser-menu   [app]
  ;------------------------------------------------------------

  (div  {:style {:paddingBottom "10px"}}

        (c/div nil
               (div
             (cond
              (= (-> app :ui :tab-browser) "company")
              {:className  ""
               :onClick        (fn[e] (write-ui  app  [:ui :tab-browser]  "top companies"))
               :onTouchStart   (fn[e] (write-ui app [:ui :tab-browser]  "top companies"))
               :style {:textDecoration "underline"
                       :display "inline-block"}
               })
             (if (= (-> app :ui :tab-browser) "company") "Back" ""))


           (c/div
              {:style {
                               :display "inline-block"
                               :width "20px"}
                   } "")

               "")
))









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
;------------------------------------------------------------

  (c/div {:style {
                :height       (- (-> app :view :height) 100)
                :zIndex       "1"
                }}

       (c/h2 nil "Companator")
       (c/h3 nil "Give and get startup advice")

           (c/ul
            {:className  "nav nav-tabs"}

            (c/li {:className  (if (= (-> app :ui :tab) "browser") "active" "")   }
                    (c/a {:className  ""
                                :onClick        (fn[e] (c/write-ui app [:ui :tab]  "browser"))
                                :onTouchStart   (fn[e] (c/write-ui app [:ui :tab]  "browser"))

                                } "Browse"))

            (c/li {:className  (if (= (-> app :ui :tab) "request") "active" "") }
                    (c/a {:className  ""
                                :onClick        (fn[e] (c/write-ui app [:ui :tab]  "request"))
                                :onTouchStart   (fn[e] (c/write-ui app [:ui :tab]  "request"))


                                } "Sign in"))



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
