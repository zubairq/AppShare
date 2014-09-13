(ns webapp.client.react.views.main
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true])
  (:use
   [webapp.client.react.views.layout.header         :only   [main-yazz-header]]
   [webapp.client.react.views.layout.main-layout    :only   [main-yazz-layout]]
   )
)

(c/ns-coils 'webapp.client.react.views.main)





(c/defn-ui-component     main-yazz-view   [app]

  (c/div nil
         (c/component  main-yazz-header app [:ui])
         (c/component  main-yazz-layout app [:ui])
         )
  )

