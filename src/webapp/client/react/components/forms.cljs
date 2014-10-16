(ns webapp.client.react.components.forms
  (:require
   [webapp.framework.client.coreclient   :as c   :include-macros true]
   [cljs.core.async                      :refer   [put! chan <! pub timeout]]
   [om-sync.core                         :as      async]
   )

  (:use
   [webapp.client.ui-helpers                :only  [validate-email]]
   [webapp.framework.client.ui-helpers      :only  [basic-input-box]]
   [clojure.string                          :only  [blank?]])

  (:require-macros
   [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils
                                               sql
                                               log
                                               neo4j
                                               defn-ui-component
                                               a
                                               div
                                               write-ui
                                               read-ui
                                               ==ui  -->ui  watch-ui <--ui
                                               <--data -->data
                                               remote
                                               input
                                               component
                                               h1 h2 h3 h4 h5 h6
                                               span
                                               ]]
))


(ns-coils 'webapp.client.react.components.forms)





;------------------------------------------------------------
(defn-ui-component    from-email-field  [ui-data]
  ;------------------------------------------------------------
  (div
   nil
   (div  {:style {:font-size "15px"
                  :display "inline-block"
                  :marginLeft "20px"}}
         "My e-mail address is:")

   (input {:value (read-ui ui-data [:value])
           :onChange    #(write-ui  ui-data  [:value]  (.. %1 -target -value))
           :style {:font-size "15px"
                   :display "inline-block"
                   :marginLeft "5px"}} "")

   ))





;------------------------------------------------------------
(defn-ui-component    password-field  [ui-data]
  ;------------------------------------------------------------
  (div {
        :style       {:width "100%" }}

       (input
        {:type        "text"
         :value       (read-ui  ui-data [:password])
         :onChange    #(write-ui  ui-data  [:password]  (.. %1 -target -value))
         :disabled    (if (read-ui  ui-data [:new-member]) "true" "")
         :onClick     #(write-ui  ui-data [:new-member] false)
         :style       {:width "100%"
                       :display "inline-block"
                       :marginLeft "5px"
                       :font-size "15px"}
         } )
       ))





;------------------------------------------------------------
(defn-ui-component    show-connection-confirmation-dialog-box  [dialog-data]
  ;------------------------------------------------------------
  (if (get-in dialog-data [:show-connection-confirmation])
    (div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "90%"
                  :height  "90%"
                  :border            "solid 1px black;"   :zIndex  "2000"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(c/write-ui  dialog-data [:show-connection-confirmation] false)
          :onClick      #(c/write-ui  dialog-data [:show-connection-confirmation] false)
          }

         (div {:style { :vertical-align "center" }}
              (div {:style {:padding "5px" :padding-bottom "30px"}} "You have now joined connectToUs!")

              (div {:style {:padding "5px"}} (str " "
                                                  (get-in dialog-data [:from-email :value] )
                                                  ))))))





;------------------------------------------------------------
(defn-ui-component   request-form   [ui-data]
  ;------------------------------------------------------------

  (div
   nil

   (if [get-in ui-data [:show-connection-confirmation]]
     (component  show-connection-confirmation-dialog-box  ui-data []))



   (div
    {:style {:border "1px solid" :padding "15px" :marginTop "10px"}}

    (div {:style {:color "rgb(228, 121, 17);"}} (str "Sign in"))
    (h5 {:style {:color "rgb(228, 121, 17);"}} (str "What is your e-mail address?"))

    (component   from-email-field   ui-data [:from-email] )



    (h5 {:style {:color "rgb(228, 121, 17);"}} (str "Do you have a Companator password?"))

    (div {:style {:display "block" :font-size "15px" }}
         (input {:type "radio" :name "member" :value "new_member"
                 :checked (if (read-ui ui-data [:new-member]) "true" "")
                 :onClick #(write-ui  ui-data [:new-member] true)
                 :style {
                         :font-size "15px"
                         :marginLeft "20px"

                         }
                 } "No, I am a new member"))


    (div {:style {:display "block" :font-size "15px" }}
         (input {:type "radio" :name "member" :value "current_member"
                 :checked (if (read-ui ui-data [:new-member]) "" "true")
                 :onClick #(write-ui  ui-data [:new-member] false)
                 :style {:display "inline-block"
                         :font-size "15px"
                         :marginLeft "20px"
                         }} "Yes, I have a password:")
         (div  {:style       #js {:width "100px" :display "inline-block"}}
               (c/component   password-field   ui-data [] )
                   "")
           "")

    (a {:href "#" :className "btn btn-warning"
        :style {:marginTop "5px"}
         :onClick     #(write-ui  ui-data [:clicked] true)
        }
         "Sign in using our secure server"
         (span {:className "glyphicon glyphicon-play"}

                 ))




    (if (not (blank?
              (get-in ui-data [:submit :message])))

      (if (not (get-in ui-data [:confirmed]))
        (c/div nil (str "Please check your Inbox for "
                        (-> ui-data :from-email :value) " to confirm your email address")))
      ))))







(==ui [:ui :request :clicked] true
      (do
        (-->ui [:ui :request :clicked] false)
        (cond


         (= (<--ui [:ui :request :new-member]) true)
         (do
           (-->data [:remote :new-member :from-email :value]
                    (<--ui [:ui :request :from-email :value]))

           (-->data [:remote :new-member :submit] true))



         (= (<--ui [:ui :request :new-member]) false)
         (do
           (-->data [:remote :sign-in :from-email :value]
                    (<--ui [:ui :request :from-email :value]))

           (-->data [:remote :sign-in :password :value]
                    (<--ui [:ui :request :password]))

           (-->data [:remote :sign-in :submit] true)
         ))))



