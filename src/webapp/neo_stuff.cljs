(ns webapp
 (:refer-clojure :exclude [val empty remove find next parents])
    (:require
        [cljs.reader                     :as reader]
        [cljs.core.async                 :as async :refer [chan close!]]
    )

  (:require-macros
    [cljs.core.async.macros :refer [go alt!]])

  (:use
        [webapp.framework.client.coreclient  :only [remote]]
        [webapp.framework.client.neo4j       :only [neo4j-fn]]
    )
    (:use-macros
        [webapp.framework.client.coreclient  :only [ns-coils sql log neo4j]]
        [webapp.framework.client.neo4j       :only [neo4j]]
     )
)
(ns-coils 'webapp)


; return the raw data for a neo4j node
(comment go
    (.log js/console (str (<! (neo4j "START x = node(*) RETURN count(x) LIMIT 1" {} ))))
)




(comment go (log (str (<!
  (neo4j
   "create (u:User { email : { email2 }, title : 'Developer' }) return u"
   {:email2 "dfsfdsf@gmail.com"} "u")))))


(comment go (log (str (<!
  (neo4j
   "create (u:User { email : { email2 }, title : 'Developer' }) return u"
   {:email2 "dffds@gmail.com"} "u")))))







; return just the data for a neo4j node (still a lot!)
(comment go
 (.log js/console
       (str
        (neo-result
         (<! (neo4j "START x = node(17109) RETURN x" {} ))
         "x"))))




(comment go
     (<! (add-to-simple-point-layer {:name "bella centre" :x 0.12 :y 0.1} "ore2")))


(comment go
     (log (<! (find-names-within-distance   "ore2"  0   0  1000))) )


;(delete-all-neo-4j-nodes :are-you-sure? "yes")




