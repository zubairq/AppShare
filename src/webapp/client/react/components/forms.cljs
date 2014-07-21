(ns webapp.client.react.components.forms
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )

  (:use
   [webapp.client.ui-helpers                :only  [validate-email
                                                    ]]
   [webapp.framework.client.ui-helpers      :only  [blur-field
                                                    update-field-value
                                                    basic-input-box ]]

   [webapp.framework.client.coreclient      :only  [log  remote  component-fn]]

   [clojure.string :only [blank?]])

  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component  ns-coils  div  component]]))

(ns-coils 'webapp.client.react.components.forms)








;------------------------------------------------------------
(defn-ui-component    from-email-field  [ui-data]
    {:absolute-path [:ui :request]}
  ;------------------------------------------------------------
  (dom/div
   nil
   (basic-input-box :field       ui-data
                    :text        "Your company email"
                    :placeholder "john@microsoft.com"
                    :error       "Email validation error"
                    )



            (if (get-in ui-data [:confirmed])
              (dom/div  #js {:className "alert alert-success"}
                        (dom/a  #js {:href "#"
                                     :className "alert-link"}
                                "Your email confirmed"
                                )))
            ))






;------------------------------------------------------------
(defn-ui-component  to-email-field  [ui-data]
    {:absolute-path [:ui :request]}
  ;------------------------------------------------------------

  (dom/div
   nil
   (basic-input-box :field       ui-data
                    :text        "Their company email"
                    :placeholder "pete@ibm.com"
                    :error       "Email validation error"
                    )



            (if (get-in ui-data [:confirmed])

              (dom/div  #js {:className "alert alert-success"}
                        (dom/a  #js {:href "#"
                                     :className "alert-link"}
                                "Their email confirmed"
                                )))))





;------------------------------------------------------------
(defn-ui-component    show-connection-confirmation-dialog-box  [dialog-data]
  {}
  ;------------------------------------------------------------
  (if (get-in dialog-data [:show-connection-confirmation])
    (div {:style {:position          "absolute"           :left    "5%"  :top "5%"
                  :width   "90%"
                  :height  "90%"
                  :border            "solid 1px black;"   :zIndex  "2000"
                  :background-color  "white"              :opacity "1.0"
                  :text-align        "center"
                  }
          :onTouchStart #(om/update! dialog-data [:show-connection-confirmation] false)
          :onClick      #(om/update! dialog-data [:show-connection-confirmation] false)
          }

         (div {:style { :vertical-align "center" }}
              (div {:style {:padding "5px" :padding-bottom "30px"}} "Your connection has been made!")

              (div {:style {:padding "5px"}} (str "From "
                                                  (get-in dialog-data [:from-email :value] ) " to "
                                                  (get-in dialog-data [:to-email   :value])))
              ))))





;------------------------------------------------------------
(defn-ui-component   request-form   [ui-data]
    {:absolute-path [:ui :request]}
;------------------------------------------------------------

  (div
   nil

   (if [get-in ui-data [:show-connection-confirmation]]
     (component  show-connection-confirmation-dialog-box  ui-data []))



   (div
    nil
    (component   from-email-field   ui-data [:from-email] )

    (component  to-email-field      ui-data [:to-email] )


    (dom/button #js {:onClick (fn [e]
                                (om/update! ui-data [:submit :value]  true))
                     :style
                     #js {:margin-top "10px"}}

                "Connect")

    (if (not (blank?
              (get-in ui-data [:submit :message])))

      (div nil (str "Please check your Inbox for "
                    (-> ui-data :from-email :value) " to confirm your email address"))
))))




