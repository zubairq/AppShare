(ns webapp.client.react.components.footer-row
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
  (:use
   [webapp.client.react.components.footer-cell  :only   [empty-footer-cell]]
   )
)

(c/ns-coils 'webapp.client.react.components.footer-row)



(c/defn-ui-component     footer-row   [t]
  (c/tr {}
        (c/div {:style {:display "inline-block"}}
               (c/component  empty-footer-cell t []))
        ))
