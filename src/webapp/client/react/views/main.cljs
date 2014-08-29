(ns webapp.client.react.views.main
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.client.react.views.main)





(c/defn-ui-component     main-yazz-view   [app]
  {:absolute-path []}

  (c/div nil
       (c/h2 nil "Yazz")
       "Yazz.com"))
