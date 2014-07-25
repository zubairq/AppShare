(ns webapp.client.react.components.connection-graph
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )

  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils div a component write-ui]])

  (:use
   [webapp.framework.client.system-globals  :only  [touch]]
   [webapp.framework.client.coreclient      :only  [log amend-record  component-fn write-ui-fn]]
   )
  )
(ns-coils 'webapp.client.react.components.connection-graph)






;---------------------------------------------------------
(defn-ui-component   text-graph    [companies]
  {:absolute-path [:ui :companies]}
  ;---------------------------------------------------------
  (div  {:style {:height "100%" :width "100%"}}

        (let [all-company-records    (-> companies :values )]

          (apply
           dom/div nil

           (map
            (fn[company-ui-record]
              (div  nil

                    (div  {:style { :width "200px"  :display "inline-block"}}
                          (get company-ui-record "company"))

                    (a {:href "#" :onClick
                        #(write-ui companies [:values]
                                     (amend-record (into [] (get @companies :values))
                                                   "company"
                                                   (get @company-ui-record "company")
                                                   (fn[z] (merge z {:clicked true}))
                                                   ))


                        }
                       (get  company-ui-record "inbound"))))

            all-company-records)
           ))))




(defn-ui-component   graph    [data]
  {:absolute-path [:ui :companies]}

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

))







(defn pad [x] (if (= (count (str x)) 1) (str "0" x) x))



;---------------------------------------------------------
(defn-ui-component  latest-endorsements    [endorsements]
  {:absolute-path [:ui :latest-endorsements]}
;---------------------------------------------------------

     (div
      {:style #js {:height "100%" :width "100%"}}

      (apply
       dom/div
       nil
       (map
        (fn[x]
          (div
           nil
           (div
            {
                 :style
                 {
                      :width "30%"
                      :display "inline-block"}}
            (str

             (pad (. (js/Date. (get x "when")) getHours)) ":"
             (pad (. (js/Date. (get x "when")) getMinutes)) ":"
             (pad (. (js/Date. (get x "when")) getSeconds)) " "


                 ))
           (div
            {
                 :style
                 {
                      :width "70%"
                      :display "inline-block"}}

            (get x "to")
            (str " --> " (get x "from")
                 ))

           ))
        (-> endorsements :values ))
       )

      ))

