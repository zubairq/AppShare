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
   [webapp.client.react.components.connection-graph    :only  [graph
                                                               text-graph
                                                               latest-endorsements]]

   [webapp.client.react.components.company-details     :only  [company-details2]]
   [webapp.framework.client.system-globals  :only  [touch  remove-debug-event]]

   [webapp.framework.client.coreclient                 :only  [log
                                                               remote
                                                               component-fn
                                                               write-ui-fn]]
   )
  (:use-macros
   [webapp.framework.client.coreclient                 :only  [defn-ui-component
                                                               ns-coils
                                                               div
                                                               component
                                                               write-ui
                                                               a]]))

(ns-coils 'webapp.client.react.views.main)









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
               :onClick        (fn[e] (write-ui  app  [:ui :tab-browser]  "top companies"))
               :onTouchStart   (fn[e] (write-ui app [:ui :tab-browser]  "top companies"))
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
                   :onClick        (fn[e] (write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :onTouchStart   (fn[e] (write-ui app [:ui :tab-browser]  "latest endorsements"))
                   :style {:textDecoration "underline"
                               :display "inline-block"}
                   })
            "latest")

))

;------------------------------------------------------------
(defn-ui-component    letter-a  [data]
  {}
  ;------------------------------------------------------------
    (div nil "-----------------------------------------------"))



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
          :onTouchStart #(write-ui splash-screen-data [:show] false)
          :onClick      #(write-ui splash-screen-data [:show] false)
          }

         (div {:style { :vertical-align "center" :width "100%" :height "100%"}}

              (div {:style {:padding "5px" :padding-bottom "0px" :font-size "40"}} (str "Coils"))

              (div {:style {:padding "5px" :padding-bottom "20px"  :font-size "25"}}
                   (str "Build Neo4j Web Apps"))

              (a {:href "https://github.com/zubairq/coils"}
                 "Follow us on GitHub")

              (component   letter-a   splash-screen-data [])



              (div {:style {:padding "5px" :padding-bottom "20px"  :font-size "20"}}
                   (str "This is a basic demo app called ConnectToUs to show the basic capabilities of Coils"))



                   (div {:style {:padding "5px" :padding-bottom "20px"}} "Click to start...")
              ))))





;------------------------------------------------------------
(defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (div {:style {
                :height       (- (-> app :view :height) 100)
                :width        (-> app :view :width)
                }}

       (dom/h2 nil "ConnectToUs.co")

       (component  splash-screen  app  [:ui :splash-screen])

         (component letter-a  app [:ui])


           (dom/ul
            #js {:className  "nav nav-tabs"}

            (dom/li #js {:className  (if (= (-> app :ui :tab) "browser") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (write-ui app [:ui :tab]  "browser"))
                                :onTouchStart   (fn[e] (write-ui app [:ui :tab]  "browser"))

                                } "Search"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "request") "active" "") }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (write-ui app [:ui :tab]  "request"))
                                :onTouchStart   (fn[e] (write-ui app [:ui :tab]  "request"))


                                } "Connect"))

            (dom/li #js {:className  (if (= (-> app :ui :tab) "login") "active" "")   }
                    (dom/a #js {:className  ""
                                :onClick        (fn[e] (write-ui app [:ui :tab]  "login"))
                                :onTouchStart   (fn[e] (write-ui app [:ui :tab]  "login"))

                                } "Login"))

            )


           (cond
            (= (-> app :ui :tab) "browser")
            (component  browser-menu  app [])
           )

           (cond
            (= (-> app :ui :tab) "request")
            (component  request-form  app [:ui :request] )


            (= (-> app :ui :tab) "login")
            (component  login  app    [:ui :login])

            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "top companies"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (component  text-graph    app [:ui :companies])


             (= (-> app :ui :graph-ab-test) "SVG")
             (component  graph app
                        [:data  :data ]))


            (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "latest endorsements"))
            (cond

             ;(= (-> app :ui :graph-ab-test) "text")
             true
             (component  latest-endorsements
                        app [:ui :latest-endorsements])


             (= (-> app :ui :graph-ab-test) "SVG")
             (component  graph app
                        [:data  :data ]))


           (and
             (= (-> app :ui :tab) "browser")
             (= (-> app :ui :tab-browser) "company"))

             (component  company-details2   app [:ui :company-details])


)))
