(ns webapp.framework.client.ui-helpers
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
   [webapp.framework.client.coreclient      :only  [log remote write-ui-fn]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-app-state
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
    [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient
    :only  [defn-ui-component ns-coils div write-ui]]))

(ns-coils 'webapp.framework.client.ui-helpers)






(defn update-field-value [field e path parent-id]
  (write-ui  field  [:value]  (.. e -target -value))
  )



(defn blur-field [request path parent-id]
   (let [mode  (get-in @request [:mode])]
     (cond
      (and (= mode "empty") (not (blank? (get-in @request [:value]))))
      (write-ui  request  [:mode]  "validate")
      )))




(def a (atom
  {:a
   {
    :value "private text"
    :private true
    :something-else
    {
     :value "public text"
     }
    }
   :ab
   {
    :value "private text"
    :private true
    :something-else
    {
     :value "public text"
     :private true
     }
    }
   }
))


(defn find-in-map [a-map a-key]
  (cond
   (get a-map a-key)
   true

   :else
   false
   ))

(find-in-map (:a @a) :private)

(defn  replace-in-map [a-map find-key replace-key replace-fn]
  (cond
   (find-in-map a-map find-key)
   (assoc a-map replace-key (replace-fn (get a-map replace-key)))

   :else
   a-map
   )
  )

(replace-in-map  (-> @a :ab :something-else)  :private :value
                 (fn[x] (apply str (repeat (count x) "*"))))







(defn basic-input-box
  [& {:keys
      [
       field
       path
       parent-id
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
                  :value       (if (and @playbackmode (get-in field [:private]))
                                 (get (replace-in-map  field  :private :value
                                                  (fn[x] (apply str (repeat (count x) "*")))) :value)


                                    (get-in field [:value]))
                  :onChange    #(update-field-value  field %1 path parent-id)
                  :onBlur      #(blur-field  field path parent-id)
                  :style       #js {:width "100%"}
                  })

            (if (not (blank? (get-in field [:error])))
              (dom/div nil error)
)))

