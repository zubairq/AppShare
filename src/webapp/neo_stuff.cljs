(ns webapp.neo-stuff
 (:refer-clojure :exclude [val empty remove find next parents])
    (:require
        [cljs.reader                     :as reader]
        [cljs.core.async                 :as async :refer [chan close!]]
    )

  (:require-macros
    [cljs.core.async.macros :refer [go alt!]])

  (:use
        [webapp.framework.client.coreclient  :only [neo4j-fn remote-fn]]
    )
    (:use-macros
        [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j remote neo4j]]
     )
)
(ns-coils 'webapp.neo-stuff)


(defn delete-all-neo-4j-nodes []
  (go (neo4j "MATCH n OPTIONAL MATCH (n)-[r]-(s) DELETE n,r,s"))
  {:success true})

;(go (log (neo4j "match n return count(n);" {} "count(n)")))

;(delete-all-neo-4j-nodes)


