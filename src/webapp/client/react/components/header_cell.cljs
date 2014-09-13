(ns webapp.client.react.components.header-cell
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.components.header-cell)



(c/defn-ui-component     empty-header-cell   [t]
  (c/div {:style {:display "inline-block"}}
        (c/div nil "+"))
  )


(c/defn-ui-component     header-cell   [t]
  (c/div {:style {:display "inline-block"}}
         (c/input {:className  "form-control"
                   :value      (c/read-ui  t  [:value])} ""))
  )
