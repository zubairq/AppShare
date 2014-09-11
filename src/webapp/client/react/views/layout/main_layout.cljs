(ns webapp.client.react.views.layout.main-layout
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.views.layout.main-layout)



(c/defn-ui-component     cell   [t] {}
  (c/input {:value (c/read-ui  t  [:value])} ""))





(c/defn-ui-component     main-table   [table-ui] {}
  (c/div nil
         (c/component cell table-ui [:extra])
         (c/component cell table-ui [])))




(c/defn-ui-component     main-yazz-layout   [header-ui] {}

  (c/div nil
         (c/component main-table header-ui [])
         ))
