(ns webapp.framework.client.components.main
  (:require
   [om.core                              :as om :include-macros true]
   [webapp.framework.client.coreclient   :as c :include-macros true]
   [om.dom                               :as dom ]
   [clojure.data                         :as data]
   [clojure.string                       :as string]))

(c/ns-coils 'webapp.framework.client.components.main)







(defn select-browser [e app]
  (om/update! app [:ui :tab]  "browser"))

(defn select-request [e app]
  (om/update! app [:ui :tab]  "request"))

(defn select-login [e app]
  (om/update! app [:ui :tab]  "login"))




(c/defn-ui-component     main-view   [app]
  {:absolute-path []}

  (c/div nil
       (c/h2 nil "Coils")
       "Build webapps with Neo4j"))
