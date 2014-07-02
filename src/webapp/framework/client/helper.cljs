(ns webapp.framework.client.helper
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-app-state
                                                    playback-controls-state
                                                    reset-app-state
                                                    ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))








(defn when-path-equals [watcher path value fn-def]
  (swap! watcher conj
         {
          :type     "path equals"
          :path     path
          :value    value
          :fn       fn-def
          }))

(defn when-value-changes [watcher path fn-def]
  (swap! watcher conj
         {
          :type     "value change"
          :path     path
          :fn       fn-def
          }))



(defn amend-record [records field value amend-fn]
  (into [] (map
            (fn[x] (if (= (get x field) value) (amend-fn x) x))
            records )))




(defn when-property-equals-in-record  [watcher path field value fn-def]
 (swap! watcher conj
         {
          :type     "record property equals"
          :path     path
          :field    field
          :value    value
          :fn       fn-def
          }))




(defn when-ui-path-equals
  [path value ui-fn]

  (when-path-equals
   ui-watchers
   path
   value
   ui-fn))



(defn when-ui-value-changes
  [path ui-fn]

  (when-value-changes
   ui-watchers
   path
   ui-fn))


(defn when-ui-property-equals-in-record
  [path field value ui-fn]

  (when-property-equals-in-record
   ui-watchers
   path
   field
   value
   ui-fn))







(defn when-data-path-equals
  [path value data-fn]

  (when-path-equals
   data-watchers
   path
   value
   data-fn))



(defn when-data-value-changes
  [path data-fn]

  (when-value-changes
   data-watchers
   path
   data-fn))


(defn when-data-property-equals-in-record
  [path field value data-fn]

  (when-property-equals-in-record
   data-watchers
   path
   field
   value
   data-fn))
