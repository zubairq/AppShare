(ns webapp.client.react.components.cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.cell)



(c/defn-ui-component     empty-cell   [no-data]
  (c/div nil
        (c/input {:className  "form-control"
                   :value      ""
                   :style {:width "100px"}} "")
        )
  )



(c/defn-ui-component     cell   [t]
  (c/div nil
         (c/input {:className  "form-control"
                   :value      (c/read-ui  t  [:value])
                   :style {:width "100px"}} ""))
  )
