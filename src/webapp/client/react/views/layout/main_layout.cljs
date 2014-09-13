(ns webapp.client.react.views.layout.main-layout
  (:require
   [webapp.framework.client.coreclient    :as      c  :include-macros true])
  (:use
   [webapp.client.react.components.cell   :only    [cell]]
   [webapp.client.react.components.table  :only    [table]]
   )
)

(c/ns-coils 'webapp.client.react.views.layout.main-layout)








(c/defn-ui-component     main-yazz-layout   [header-ui] {}

  (c/div nil
         (c/component  table header-ui [])
         ))
