(ns webapp.framework.client.components.main
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom ]
   [clojure.data     :as data]
   [clojure.string   :as string])

  (:use
   [webapp.framework.client.ui-helpers
    :only  [validate-email
            validate-full-name
            validate-endorsement
            blur-field
            update-field-value
            basic-input-box ]]
   [clojure.string :only [blank?]]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils div h2]]))
(ns-coils 'webapp.framework.client.components.main)







(defn select-browser [e app]
  (om/update! app [:ui :tab]  "browser"))

(defn select-request [e app]
  (om/update! app [:ui :tab]  "request"))

(defn select-login [e app]
  (om/update! app [:ui :tab]  "login"))




;------------------------------------------------------------
(defn-ui-component     main-view   [app]
  {:absolute-path []}
;------------------------------------------------------------

  (div nil
           (h2 nil "Coils")
           "Build webapps with Neo4j"

))
