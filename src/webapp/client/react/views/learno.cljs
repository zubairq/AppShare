(ns webapp.client.react.views.learno
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.client.react.views.learno)





(c/defn-ui-component     main-learno-view   [app]
  {}

  (c/div nil
       (c/h2 nil "Learno")
       "Learn online"))
