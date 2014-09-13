(ns webapp.client.react.components.cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.cell)



(c/defn-ui-component     cell   [t]
  (c/td nil
  (c/input {:className  "form-control"
            :value      (c/read-ui  t  [:value])} ""))
  )
