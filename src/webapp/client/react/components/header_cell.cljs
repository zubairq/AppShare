(ns webapp.client.react.components.header-cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.header-cell)



(c/defn-ui-component     header-cell   [t]
  (c/th nil
  (c/input {:className  "form-control"
            :value      (c/read-ui  t  [:value])} ""))
  )
