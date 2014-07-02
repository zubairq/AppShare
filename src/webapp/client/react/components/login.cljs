(ns webapp.client.react.components.login
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [webapp.framework.client.coreclient])

  (:use
   [webapp.client.ui-helpers                :only  [validate-email
                                                    validate-full-name
                                                    validate-endorsement
                                                    blur-field
                                                    update-field-value
                                                    basic-input-box ]]
   [clojure.string :only [blank?]]
   [webapp.framework.client.system-globals :only [debugger-ui]])
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils]]))

(ns-coils 'webapp.client.react.components.login)






;------------------------------------------------------------
(defn-ui-component    login-email-field  [ui-data]
    {:absolute-path [:ui :login :login-email]}
  ;------------------------------------------------------------
  (dom/div nil (dom/div
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
            ) ))












;------------------------------------------------------------
(defn-ui-component   login   [ui-data]
    {:absolute-path [:ui :login]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (om/build  login-email-field   (-> ui-data :login-email ))

    (dom/button #js {:onClick (fn [e]
                                (js/alert (-> @ui-data :login-email :value ) )
                                )
                     :style
                     #js {:margin-top "10px"}}
                "Login")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (dom/div nil "Submitted")
))))



