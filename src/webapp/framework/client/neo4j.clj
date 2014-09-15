(ns webapp.framework.client.neo4j
  [:use [webapp.framework.server.encrypt]]
  (:use clojure.pprint)
  (:require [rewrite-clj.parser :as p])
  (:require [rewrite-clj.printer :as prn]))



(defmacro neo4j
  ([cypher-str]
  `(webapp.framework.client.neo4j.neo4j-fn
    ~(encrypt cypher-str)
    ))

  ([cypher-str params]
  `(webapp.framework.client.neo4j.neo4j-fn
    ~(encrypt cypher-str)
    ~params
    ))

( [cypher-str params return]
  `(webapp.framework.client.neo4j.neo4j-fn
    ~(encrypt cypher-str)
    ~params
    ~return
    )))




