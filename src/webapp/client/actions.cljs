(ns webapp.client.actions
  (:require
   [goog.net.cookies                     :as cookie]
   [om.core                              :as om    :include-macros true]
   [om.dom                               :as dom   :include-macros true]
   [webapp.framework.client.coreclient   :as c     :include-macros true]
   [cljs.core.async                      :refer   [put! chan <! pub timeout]]
   [om-sync.core                         :as async]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   [ankha.core                           :as ankha]
   )

   (:require-macros
    [cljs.core.async.macros :refer [go]]
    [webapp.framework.client.coreclient :refer [ns-coils  ==data  server-call
                                                remote neo4j sql sql-1 neo4j-1]]
    ))

(ns-coils 'webapp.client.actions)



(==data  [:actions :add-row] true
             (server-call
              (remote  add-row)
              ))


(comment go (.log js/console
      (pr-str (neo4j "match (n:Table) return n;" {} "n"))))

(comment go (.log js/console
       (pr-str (sql "select count(*) from ojobs_users;" {}))))
