(ns webapp.client.react.components.login
  (:require
   [om.core                              :as om :include-macros true]
   [om.dom                               :as dom :include-macros true]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   [webapp.framework.client.coreclient   :as c :include-macros true]
   )

  (:use
   [webapp.client.ui-helpers                :only  [validate-email]]
   [webapp.framework.client.ui-helpers      :only  [blur-field
                                                    update-field-value
                                                    basic-input-box ]]
   [webapp.client.react.components.forms    :only  [from-email-field]]

   [clojure.string :only [blank?]])
  )

(c/ns-coils 'webapp.client.react.components.login)






;------------------------------------------------------------
(c/defn-ui-component    login-email-field  [ui-data]
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
(c/defn-ui-component   login   [ui-data]
    {:absolute-path [:ui :login]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (c/component  login-email-field   ui-data [:login-email] )

    (c/component   from-email-field   ui-data [:from-email] )


    (dom/button #js {:onClick (fn [e]
                                (do
                                  (js/alert (-> @ui-data :login-email :value ) )
                                  (c/write-ui
                                   ui-data [:submit :message] "Submit not working" )))
                     :style
                     #js {:margin-top "10px"}}
                "Login")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (dom/div nil (str (get-in ui-data [:submit :message])))
))))



