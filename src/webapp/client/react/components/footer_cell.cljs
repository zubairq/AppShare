(ns webapp.client.react.components.footer-cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.footer-cell)



(c/defn-ui-component     empty-footer-cell   [no-data]
  (c/div nil
        (c/input {:className  "form-control"
                   :value      "+ Data"
                   :style {:width "100px"}} "")
        )
  )


