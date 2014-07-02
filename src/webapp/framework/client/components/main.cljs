(ns webapp.framework.client.components.main
  (:require
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [webapp.framework.client.coreclient])

  (:use
   [webapp.framework.client.ui-helpers                :only  [validate-email
                                                    validate-full-name
                                                    validate-endorsement
                                                    blur-field
                                                    update-field-value
                                                    basic-input-box ]]
   [clojure.string :only [blank?]]
   )
  (:use-macros
   [webapp.framework.client.coreclient      :only  [defn-ui-component ns-coils]]))
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

  (dom/div nil
           (dom/h2 nil "Coils")
           "Build webapps with Neo4j"

))
