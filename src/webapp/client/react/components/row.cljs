(ns webapp.client.react.components.row
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
  (:use
   [webapp.client.react.components.cell  :only   [cell  empty-cell]]
   )
)

(c/ns-coils 'webapp.client.react.components.row)



(c/defn-ui-component     row   [t]
  (c/div {}
         (c/div {:style {:display "inline-block"}}
                (c/component  cell       t []))
         ))
