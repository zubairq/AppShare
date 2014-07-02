(ns webapp.client.react.components.forms
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
   [clojure.string :only [blank?]])
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils]]))
(ns-coils 'webapp.client.react.components.forms)





;------------------------------------------------------------
(defn-ui-component     full-name-field   [field]
  {:absolute-path [:ui :request]}
;------------------------------------------------------------

  (dom/div
   nil
   (basic-input-box :field       field
                    :text        "Your full name"
                    :placeholder "John Smith"
                    :error       "Full name must be at least 6
                                  characters and contain a space"
                    )
   ))






;------------------------------------------------------------
(defn-ui-component   to-full-name-field  [ui-data]
  {:absolute-path [:ui :request]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (basic-input-box :field       ui-data
                     :text        "Their full name"
                     :placeholder "Pete Austin"
                     :error       "Full name must be at least 6
                                   characters and contain a space"
                     ))))





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
(defn-ui-component   endorsement-field   [ui-data]
    {:absolute-path [:ui :request]}
;------------------------------------------------------------
  (dom/div
   nil
   (basic-input-box :field       ui-data
                    :text        "Skill your company has"
                    :placeholder "marketing"
                    :error       "Endorsement not valid"
                    )
   ))





;------------------------------------------------------------
(defn-ui-component   request-form-old   [ui-data]
    {:absolute-path [:ui :request]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (dom/div #js {:style #js {:padding-top "40px"}} "Your details (at your company)")

    (om/build full-name-field    (get-in ui-data [:from-full-name]))
    (om/build from-email-field   (-> ui-data :from-email ))

    (dom/div #js {:style #js {:padding-top "40px"}} " Them ")

    (om/build to-full-name-field  (-> ui-data :to-full-name ))
    (om/build to-email-field      (-> ui-data :to-email ))



    (dom/div
     #js {:style #js {:padding-top "40px"}}
     "The expertise your company has you want them to endorse")


    (dom/div #js {:className "input-group"}
             (om/build endorsement-field  (-> ui-data :endorsement )))

    (dom/button #js {:onClick (fn [e]
                                (om/update! ui-data [:submit :value]  true))
                     :style
                     #js {:margin-top "40px"}}
                "Send request")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (dom/div nil "Submitted")
))))



;------------------------------------------------------------
(defn-ui-component   request-form   [ui-data]
    {:absolute-path [:ui :request]}
;------------------------------------------------------------

  (dom/div
   nil
   (dom/div
    nil
    (om/build from-email-field   (-> ui-data :from-email ))
    (dom/div #js {:className "input-group"}
             (om/build endorsement-field  (-> ui-data :endorsement )))

    (dom/div
     #js {:style #js {:padding-top "40px"}})

    (om/build to-email-field      (-> ui-data :to-email ))


    (dom/button #js {:onClick (fn [e]
                                (om/update! ui-data [:submit :value]  true))
                     :style
                     #js {:margin-top "10px"}}
                "Ask for endorsement")

    (if (not (blank?
              (get-in ui-data [:submit :message])))
      (dom/div nil "Submitted")
))))




