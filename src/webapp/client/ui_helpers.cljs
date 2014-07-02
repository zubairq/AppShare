(ns webapp.client.ui-helpers
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))





(defn update-field-value [field e]
  (om/update! field [:value] (.. e -target -value))
  )



(defn blur-field [request]
   (let [mode  (get-in @request [:mode])]
     (cond
      (and (= mode "empty") (not (blank? (get-in @request [:value]))))
      (om/update! request [:mode]  "validate")
      )))





(defn validate-full-name [full-name]
  (if (and (> (count full-name) 6) (pos? (.indexOf full-name " ") ))
    true
    ))

(defn validate-endorsement [full-name]
  (if (> (count full-name) 2)
    true
    ))


(defn validate-email [email]
  (if (pos? (.indexOf (str email) "@"))
    true
    ))







(defn basic-input-box
  [& {:keys
      [
       field
       text
       placeholder
       error
       ]
      :or
      {
       text           "No field name"
       placeholder    "Placeholder"
       error          "Error in field"
       }
      }]
  (dom/div #js {:className "input-group"
                :style #js {:marginTop "5px"}}

           (dom/span
            #js {:className "input-group-addon" :style #js {:width "200px"}}
             (str text))

            (dom/input
             #js {:type        "text"
                  :className   "form-control"
                  :placeholder placeholder
                  :value       (get-in field [:value])
                  :onChange    #(update-field-value  field %1)
                  :onBlur      #(blur-field  field)
                  :style       #js {:width "100%"}
                  })

            (if (not (blank? (get-in field [:error])))
              (dom/div nil error)
)))

