(ns webapp.client.react.components.forms
  (:require
   [webapp.framework.client.coreclient   :as c   :include-macros true]
   )

  (:use
   [webapp.client.ui-helpers                :only  [validate-email]]
   [webapp.framework.client.ui-helpers      :only  [basic-input-box]]
   [clojure.string                          :only  [blank?]]))

(c/ns-coils 'webapp.client.react.components.forms)








;------------------------------------------------------------
(c/defn-ui-component    from-email-field  [ui-data]
    {:absolute-path [:ui :request]}
  ;------------------------------------------------------------
  (c/div
   nil
   (basic-input-box :path        path
                    :parent-id   parent-id
                    :field       ui-data
                    :text        "My e-mail address is:"
                    :placeholder "john@microsoft.com"
                    :error       "Email validation error"
                    )

   (if (get-in ui-data [:confirmed])
              (c/div  {:className "alert  alert-success"}
                        (c/a  {:href "#"
                                     :className "alert-link"}
                                "Your email confirmed. Your company is now listed in connectToUs.co"
                                )))))





;------------------------------------------------------------
(c/defn-ui-component    password-field  [ui-data]
    {:absolute-path [:ui :request]}
  ;------------------------------------------------------------
  (c/div
   nil
   (c/input
             #js {:type        "text"
                  :className   "form-control"
                  :value       ""
                  :style       #js {:width "100px" :display "inline-block"}
                  } "")
   ""
   ))





;------------------------------------------------------------
(c/defn-ui-component    show-connection-confirmation-dialog-box  [dialog-data]
  {}
  ;------------------------------------------------------------
  (if (get-in dialog-data [:show-connection-confirmation])
    (c/div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "90%"
                  :height  "90%"
                  :border            "solid 1px black;"   :zIndex  "2000"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(c/write-ui  dialog-data [:show-connection-confirmation] false)
          :onClick      #(c/write-ui  dialog-data [:show-connection-confirmation] false)
          }

         (c/div {:style { :vertical-align "center" }}
              (c/div {:style {:padding "5px" :padding-bottom "30px"}} "You have now joined connectToUs!")

              (c/div {:style {:padding "5px"}} (str " "
                                                  (get-in dialog-data [:from-email :value] )
                                                  ))))))





;------------------------------------------------------------
(c/defn-ui-component   request-form   [ui-data]
  ;------------------------------------------------------------

  (c/div
   nil

   (if [get-in ui-data [:show-connection-confirmation]]
     (c/component  show-connection-confirmation-dialog-box  ui-data []))



   (c/div
    {:style {:border "1px solid" :padding "15px" :marginTop "10px"}}

    (c/div nil (str "Sign in"))
    (c/h5 nil (str "What is your e-mail address?"))

    (c/component   from-email-field   ui-data [:from-email] )



    (c/h5 nil (str "Do you have a Companator password?"))

    (c/input {:type "radio" :name "member" :value "new_member"} "No, I am a new member")



    (c/div {:style {:display "block"}}
           (c/input {:type "radio" :name "member" :value "current_member"
                     :style {:display "inline-block"}} "Yes, I have a password:")
           (c/div  {:style       #js {:width "100px" :display "inline-block"}}
                   (c/component   password-field   ui-data [:from-email] )
                   "")
           "")

    (c/a {:href "#" :className "btn btn-warning"}
         "Sign in using our secure server"
         (c/span {:className "glyphicon glyphicon-play"}

                 ))




    (if (not (blank?
              (get-in ui-data [:submit :message])))

      (if (not (get-in ui-data [:confirmed]))
        (c/div nil (str "Please check your Inbox for "
                        (-> ui-data :from-email :value) " to confirm your email address")))
      ))))




