(ns webapp.client.react.components.connection-graph
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils]])
  (:use
   [webapp.client.helper  :only [amend-record]]
   [webapp.framework.client.system-globals  :only  [touch]]
   ))
(ns-coils 'webapp.client.react.components.connection-graph)






;---------------------------------------------------------
(defn-ui-component   text-graph    [companies]
  {:absolute-path [:ui :companies]}
  ;---------------------------------------------------------
     (dom/div
      #js {:style #js {:height "100%" :width "100%"}}

      (apply
       dom/div
       nil
       (map
        (fn[x]
          (dom/div
           nil
           (dom/div
            #js {
                 :style
                 #js {
                      :width "200px"
                      :display "inline-block"}}
            (get x "company"))
           (dom/a
            #js {:href "#"
                 :onClick

                 #(om/update! companies [:values]
                              (amend-record (into [] (get @companies :values))
                                            "company"
                                            (get @x "company")
                                            (fn[z] (merge z {:clicked true}))
                                            ))


                 }
            (get x "inbound"))
           ))
        (-> companies :values ))
       )

      ))




(defn graph [{:keys [data]} owner]
  (reify

    ;---------------------------------------------------------
    om/IRender
    (render
     [this]
     (dom/div
      #js {:style #js {:height "100%" :width "100%"}}

      (dom/div #js {:style #js {:padding-top "40px"}} "Connections")

      (dom/svg #js {:style #js {:width "200" :height "200"}}
          (dom/circle #js {:cx "60"
                           :cy "60"
                           :r  (get-in data [:width])
                           :stroke "green"
                           :strokeWidth "4"
                           :fill "yellow"} )
      )

))))







(defn pad [x] (if (= (count (str x)) 1) (str "0" x) x))



;---------------------------------------------------------
(defn-ui-component  latest-endorsements    [endorsements]
  {:absolute-path [:ui :latest-endorsements]}
;---------------------------------------------------------

     (dom/div
      #js {:style #js {:height "100%" :width "100%"}}

      (apply
       dom/div
       nil
       (map
        (fn[x]
          (dom/div
           nil
           (dom/div
            #js {
                 :style
                 #js {
                      :width "50%"
                      :display "inline-block"}}
            (str

             (pad (. (js/Date. (get x "when")) getHours)) ":"
             (pad (. (js/Date. (get x "when")) getMinutes)) ":"
             (pad (. (js/Date. (get x "when")) getSeconds)) " "


             (get x "from")
                 ))
           (dom/div
            #js {
                 :style
                 #js {
                      :width "50%"
                      :display "inline-block"}}
            (str (get x "skill") " --> " (get x "to")
                 ))

           ))
        (-> endorsements :values ))
       )

      ))

