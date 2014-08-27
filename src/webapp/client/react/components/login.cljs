(ns webapp.client.react.components.login
  (:require
   [om.core                              :as om :include-macros true]
   [webapp.framework.client.coreclient   :as c :include-macros true]
   )

  (:use
   [webapp.client.ui-helpers                :only  [validate-email]]
   [webapp.framework.client.ui-helpers      :only  [basic-input-box]]
   [webapp.client.react.components.forms    :only  [from-email-field]]
   [clojure.string :only [blank?]])
  )

(c/ns-coils 'webapp.client.react.components.login)






;------------------------------------------------------------
(c/defn-ui-component    login-email-field  [ui-data]
  {:absolute-path [:ui :login :login-email]}
  ;------------------------------------------------------------
  (c/div nil
         (c/div
          nil

          (basic-input-box :path        path
                           :parent-id   parent-id
                           :field       ui-data
                           :text        "Your login email"
                           :placeholder "john@microsoft.com"
                           :error       "Email validation error"
                           )



          (if (get-in ui-data [:confirmed])
            (c/div  {:className "alert alert-success"}

                    (c/a  {:href "#"  :className "alert-link"}

                          "Your email confirmed"
                          ))))))












;------------------------------------------------------------
(c/defn-ui-component   login   [ui-data]
  {:absolute-path [:ui :login]}
  ;------------------------------------------------------------

  (c/div
   nil
   (c/div
    nil
    (c/component  login-email-field   ui-data [:login-email] )

    (c/component   from-email-field   ui-data [:from-email] )


    (c/button {:onClick (fn [e]
                          (do
                            (js/alert (-> @ui-data :login-email :value ) )
                            (c/write-ui
                             ui-data [:submit :message] "Submit not working" )))
               :style
               {:margin-top "10px"}
               :type        "button"
               :className   (str "btn btn-default btn-lg")
               }
              "Login")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (c/div nil (str (get-in ui-data [:submit :message])))
      ))))



