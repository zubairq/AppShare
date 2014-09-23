(ns webapp.client.react.components.table
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
  (:use
   [webapp.client.react.components.cell         :only   [cell]]
   [webapp.client.react.components.row          :only   [row]]
   [webapp.client.react.components.header-row   :only   [header-row]]
   [webapp.client.react.components.footer-row   :only   [footer-row]]
   )
)

(c/ns-coils 'webapp.client.react.components.table)






(c/defn-ui-component     table   [table-ui] {}
         (c/div {}
                ;(c/component  header-row  table-ui [:extra])
                (c/div nil (str (count (c/read-ui  table-ui [:table :rows])) " rows"))
                (c/add-many
                  (map
                   (fn [input]
                    (c/component  row   table-ui [:table]))
                   (c/read-ui  table-ui [:table :rows])

                 ))
                (c/component  footer-row  table-ui [:actions])
                  "")
         )

