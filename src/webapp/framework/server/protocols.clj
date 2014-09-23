(ns webapp.framework.server.protocols
  (:use [clojure.pprint])
  (:use [webapp-config.settings])
  (:use [webapp.framework.server.records])
  (:import [webapp.framework.server.records NeoNode])
)

(defprotocol INeoNode
  (id [this] 1)
  (setProp [this property value] nil)
)


(extend-type NeoNode
  INeoNode
  (id [this]  "Html")
  (setProp [this property value] (assoc this property value)))

