(ns webapp.neo-stuff
 (:refer-clojure :exclude [val empty remove find next parents])
    (:require
        [cljs.reader                     :as reader]
        [cljs.core.async                 :as async :refer [chan close!]]
    )

  (:require-macros
    [cljs.core.async.macros :refer [go alt!]])

    (:use-macros
        [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j neo4j-1 sql-1 log]]
     )
)
(ns-coils 'webapp.neo-stuff)


;(go (<! (add-to-simple-point-layer {:name "ny ny" :x 0.12 :y 0.1} "ore2")))
;(go (log (<! (find-names-within-distance   "usa"  0   0  1000))) )



;(go (log (neo4j "match n return count(n)")))
;(go (log (sql "select count(*) from yazz_login_details" {})))

;(go (log  (sql-1 "select count(*) from yazz_login_details
;                 where groups = 'LEARNO_USER'" {})))

;(go (log  (sql-1 "select count(*) from learno_tests " {})))

