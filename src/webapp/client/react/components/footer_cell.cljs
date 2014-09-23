(ns webapp.client.react.components.footer-cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.footer-cell)



(c/defn-ui-component     empty-footer-cell   [actions]
  (c/div nil
         (c/input {:className  "form-control"
                   :value      "+ Data"
                   :style      {:width "100px"}
                   :onClick    #(c/write-ui  actions [:add-row] true)

                   } ""
                  )
         )
  )




(c/==ui  [:ui  :table :actions  :add-row]   true

         (do

           (c/-->ui   [:ui  :table :actions  :add-row] false)
           (c/-->data [:actions :add-row] true)

           ))



