(ns webapp.framework.client.neo4j
    (:refer-clojure :exclude [val empty remove find next parents])
    (:require
        [cljs.core.async :as async :refer         [chan close!]]
        [clojure.string]
    )
    (:use
        [webapp.framework.client.coreclient :only  [remote-fn]]
        [webapp.framework.client.records    :only  [NeoNode map->NeoNode]]
    )
    (:require-macros
      [cljs.core.async.macros :refer                 [go alt!]]
     )
    (:use-macros
        [webapp.framework.client.coreclient :only  [ns-coils log remote]]
        [webapp.framework.client.neo4j :only       [neo4j]]
     )
)
(ns-coils 'webapp.framework.client.neo4j)


(cljs.reader/register-tag-parser! "webapp.framework.server.records.NeoNode" map->NeoNode)


;----------------------------------------------------------
(defn neo4j-fn
  "Call the server side neo4j function"
  ([cypher-str]
  ;----------------------------------------------------------
  (go
   (remote
        "!neo4j"
        {
         :cypher    cypher-str
         :params    {}})))
  ([cypher-str params]
  ;----------------------------------------------------------
  (go
   (remote
        "!neo4j"
        {
         :cypher    cypher-str
         :params    params})))

([cypher-str   params   return]
  ;----------------------------------------------------------
  (go
   (remote
        "!neo4j_nodes"
        {
         :cypher    cypher-str
         :params    params
         :return    return}))))









;----------------------------------------------------------
(defn add-to-simple-point-layer
  [item  layer-name]
;----------------------------------------------------------
    (go
        (remote
             "!add-to-simple-point-layer"
             {:node         item
              :layer-name   layer-name})))






;----------------------------------------------------------
(defn find-names-within-distance
  [layer-name  x  y  km]
  ;----------------------------------------------------------
    (go
        (remote
             "!find-names-within-distance"
             {:layer-name layer-name
              :x          x
              :y          y
              :dist-km    km})))




;----------------------------------------------------------
(defn find-names-within-bounds
  [layer-name  min-x  min-y  max-x  max-y]
  ;----------------------------------------------------------
  (go
   (remote "!find-names-within-bounds"   {:layer-name layer-name
                                              :min-x min-x
                                              :min-y min-y
                                              :max-x max-x
                                              :max-y max-y
                                              })))

