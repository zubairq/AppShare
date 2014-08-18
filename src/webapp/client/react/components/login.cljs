(ns webapp.client.react.components.login
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string])

  (:use
   [webapp.client.ui-helpers                :only  [validate-email]]
   [webapp.framework.client.coreclient      :only  [log remote component-fn write-ui-fn]]
   [webapp.framework.client.ui-helpers      :only  [blur-field
                                                    update-field-value
                                                    basic-input-box ]]
   [webapp.client.react.components.forms    :only  [from-email-field]]

   [clojure.string :only [blank?]]
   [webapp.framework.client.system-globals  :only [debugger-ui  remove-debug-event]])

  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component  ns-coils  component write-ui]]))

(ns-coils 'webapp.client.react.components.login)






;------------------------------------------------------------
(defn-ui-component    login-email-field  [ui-data]
    {:absolute-path [:ui :login :login-email]}
  ;------------------------------------------------------------
  (dom/div nil (dom/div
   nil

   (basic-input-box :path        path
                    :parent-id   parent-id
                    :field       ui-data
                    :text        "Your login email"
                    :placeholder "john@microsoft.com"
                    :error       "Email validation error"
                    )



            (if (get-in ui-data [:confirmed])
              (dom/div  #js {:className "alert alert-success"}

                        (dom/a  #js {:href "#"  :className "alert-link"}

                                "Your email confirmed"
                                )))
            ) ))












;------------------------------------------------------------
(defn-ui-component   login   [ui-data]
    {:absolute-path [:ui :login]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (component  login-email-field   ui-data [:login-email] )

    (component   from-email-field   ui-data [:from-email] )


    (dom/button #js {:onClick (fn [e]
                                (do
                                  (js/alert (-> @ui-data :login-email :value ) )
                                  (write-ui
                                   ui-data [:submit :message] "Submit not working" )))
                     :style
                     #js {:margin-top "10px"}}
                "Login")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (dom/div nil (str (get-in ui-data [:submit :message])))
))))



