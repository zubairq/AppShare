(ns webapp.client.react.components.connection-graph
  (:require
   [webapp.framework.client.coreclient   :as c   :include-macros true]
   ))
(c/ns-coils 'webapp.client.react.components.connection-graph)






;---------------------------------------------------------
(c/defn-ui-component   text-graph    [companies]
  {:absolute-path [:ui :companies]}
  ;---------------------------------------------------------

  (do
    ;(set! (.-innerHTML (.getElementById js/document "playback_state")) (pr-str (c/read-ui  companies [:values] )))
    (c/div  {:style {:height "100%" :width "100%"}}

        (let [all-company-records    (c/read-ui  companies [:values] )]

          (c/add-many

           (map
            (fn[company-ui-record]
              (c/div  nil

                    (c/div  {:style { :width "200px"  :display "inline-block"}}
                            (str (get
                                     company-ui-record
                                     "company"))
                            )

                    (c/a {:href "#" :onClick
                        #(c/write-ui  companies  [:values]
                                     (c/amend-record (into [] (get @companies :values))
                                                   "company"
                                                   (get @company-ui-record "company")
                                                   (fn[z] (merge z {:clicked true}))
                                                   ))


                        }
                       (pr-str (get
                         company-ui-record
                        "inbound"))

                         )))

            all-company-records)
           )
          )

            )))








(c/defn-ui-component   graph    [data]
  {:absolute-path [:ui :companies]}

  (c/div
      {:style {:height "100%" :width "100%"}}

      (c/div {:style {:padding-top "40px"}} "Connections")

      (c/svg {:style {:width "200" :height "200"}}
          (c/circle {:cx "60"
                           :cy "60"
                           :r  (get-in data [:width])
                           :stroke "green"
                           :strokeWidth "4"
                           :fill "yellow"} )
      )

))







(defn pad [link-item]
  (if (= (count (str link-item)) 1)
    (str "0" link-item)
    link-item))



;---------------------------------------------------------
(c/defn-ui-component  latest-endorsements    [endorsements]
  {:absolute-path [:ui :latest-endorsements]}
;---------------------------------------------------------

     (c/div
      {:style {:height "100%" :width "100%"}}

      (c/add-many
       (map
        (fn[link-item]
          (c/div nil
           (c/div
            {
                 :style
                 {
                      :width "30%"
                      :display "inline-block"}}
            (str
             (pad (. (js/Date. (get link-item "when")) (getHours) )) ":"
             (pad (. (js/Date. (get link-item "when")) (getMinutes) )) ":"
             (pad (. (js/Date. (get link-item "when")) (getSeconds) )) " "))

           (c/div
            {
                 :style
                 {
                      :width "70%"
                      :display "inline-block"}}

            (get link-item "to")
            (str " --> " (get link-item "from")))))

        (get endorsements :values ))
       )))

