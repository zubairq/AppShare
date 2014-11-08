(ns webapp.client.react.views.learno
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true])
  (:use
   [webapp.client.react.components.list-of-tests     :only   [component-loads-of-stuff]]
   )

  )



(c/ns-coils 'webapp.client.react.views.learno)





(c/defn-ui-component     main-learno-view   [app]
  {}

  (c/div nil
         (c/div {:style {:padding-bottom "20px"}}
                (c/h2 nil "Learno"))
         (c/component  component-loads-of-stuff  app [:ui])
         ))
