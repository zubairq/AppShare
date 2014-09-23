(ns webapp.framework.client.protocols
  (:use
   [webapp.framework.client.records :only  [NeoNode]]
   )
  )



(defprotocol INeoNode
  (id [this] :id))



(defprotocol TypeInfo
  (gettype [this] nil))



