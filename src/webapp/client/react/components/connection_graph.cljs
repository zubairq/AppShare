(ns webapp.client.react.components.connection-graph
  (:require
   [om.core                              :as om  :include-macros true]
   [webapp.framework.client.coreclient   :as c   :include-macros true]
   [om.dom                               :as dom]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   ))
(c/ns-coils 'webapp.client.react.components.connection-graph)






;---------------------------------------------------------
(c/defn-ui-component   text-graph    [companies]
  {:absolute-path [:ui :companies]}
  ;---------------------------------------------------------
  (c/div  {:style {:height "100%" :width "100%"}}

        (let [all-company-records    (c/read-ui  companies [:values] )]

          (apply
           dom/div nil

           (map
            (fn[company-ui-record]
              (c/div  nil

                    (c/div  {:style { :width "200px"  :display "inline-block"}}
                          (get company-ui-record "company"))

                    (c/a {:href "#" :onClick
                        #(c/write-ui  companies  [:values]
                                     (c/amend-record (into [] (get @companies :values))
                                                   "company"
                                                   (get @company-ui-record "company")
                                                   (fn[z] (merge z {:clicked true}))
                                                   ))


                        }
                       (get  company-ui-record "inbound"))))

            all-company-records)
           ))))








(c/defn-ui-component   graph    [data]
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
(c/defn-ui-component  latest-endorsements    [endorsements]
  {:absolute-path [:ui :latest-endorsements]}
;---------------------------------------------------------

     (c/div
      {:style #js {:height "100%" :width "100%"}}

      (apply
       dom/div
       nil
       (map
        (fn[x]
          (c/div
           nil
           (c/div
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
           (c/div
            {
                 :style
                 {
                      :width "70%"
                      :display "inline-block"}}

            (get x "to")
            (str " --> " (get x "from")))))

        (-> endorsements :values ))
       )))

