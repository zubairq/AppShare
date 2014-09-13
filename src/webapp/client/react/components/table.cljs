(ns webapp.client.react.components.table
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
  (:use
   [webapp.client.react.components.cell  :only   [cell]]
   [webapp.client.react.components.row   :only   [row]]
   )
)

(c/ns-coils 'webapp.client.react.components.table)






(c/defn-ui-component     table   [table-ui] {}
  (c/div {:className "table-responsive"}
         (c/table {:className "table"}
                  (c/component  row table-ui [:extra])
                  (c/component  row table-ui []))))
