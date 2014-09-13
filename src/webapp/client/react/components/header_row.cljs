(ns webapp.client.react.components.header-row
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
  (:use
   [webapp.client.react.components.header-cell  :only   [header-cell]]
   )
)

(c/ns-coils 'webapp.client.react.components.header-row)



(c/defn-ui-component     header-row   [t]
  (c/tr {}
         (c/component  header-cell t [])
         (c/component  header-cell t [])
         ))
