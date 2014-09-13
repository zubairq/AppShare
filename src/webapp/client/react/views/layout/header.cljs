(ns webapp.client.react.views.layout.header
  (:require
   [webapp.framework.client.coreclient   :as  c  :include-macros true])
)

(c/ns-coils 'webapp.client.react.views.layout.header)






(c/defn-ui-component     main-yazz-header   [header-ui]

  (c/div nil
         (c/h3  nil "Yazz.com")
         ))
