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
                    :text        "Your company email"
                    :placeholder "john@microsoft.com"
                    :error       "Email validation error"
                    )



            (if (get-in ui-data [:confirmed])
              (c/div  {:className "alert  alert-success"}
                        (c/a  {:href "#"
                                     :className "alert-link"}
                                "Your email confirmed"
                                )))))






;------------------------------------------------------------
(c/defn-ui-component  to-email-field  [ui-data]
    {:absolute-path [:ui :request]}
  ;------------------------------------------------------------

  (c/div
   nil
   (basic-input-box :path        path
                    :parent-id   parent-id
                    :field       ui-data
                    :text        "Their company email"
                    :placeholder "pete@ibm.com"
                    :error       "Email validation error"
                    )



            (if (get-in ui-data [:confirmed])

              (c/div  {:className "alert alert-success"}
                        (c/a  {:href "#"
                                     :className "alert-link"}
                                "Their email confirmed"
                                )))))





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
              (c/div {:style {:padding "5px" :padding-bottom "30px"}} "Your connection has been made!")

              (c/div {:style {:padding "5px"}} (str "From "
                                                  (get-in dialog-data [:from-email :value] ) " to "
                                                  (get-in dialog-data [:to-email   :value])))))))





;------------------------------------------------------------
(c/defn-ui-component   request-form   [ui-data]
  {:absolute-path [:ui :request]}
  ;------------------------------------------------------------

  (c/div
   nil

   (if [get-in ui-data [:show-connection-confirmation]]
     (c/component  show-connection-confirmation-dialog-box  ui-data []))



   (c/div
    nil
    (c/component   from-email-field   ui-data [:from-email] )

    (c/component  to-email-field      ui-data [:to-email] )


    (c/button {:disabled  (if (c/read-ui ui-data [:details-valid]) false true)
               :onClick   (fn [e] (c/write-ui  ui-data [:submit :value]  true))
               :style     {:margin-top "10px"}
               :type      "button"
               :className (str "btn btn-default btn-lg"
                                (if (not (c/read-ui ui-data [:details-valid]))
                                  " btn-disabled"))}

              "Connect")

    (if (not (blank?
              (get-in ui-data [:submit :message])))

      (c/div nil (str "Please check your Inbox for "
                      (-> ui-data :from-email :value) " to confirm your email address"))
      ))))




