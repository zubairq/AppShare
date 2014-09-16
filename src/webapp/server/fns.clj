(ns webapp.server.fns
  [:require [clojure.string :as str]]
  [:use [korma.db]]
  [:use [webapp.framework.server.systemfns]]
  [:use [webapp.framework.server.email-service]]
  [:use [webapp.framework.server.encrypt]]
  [:use [korma.core]]
  [:use [clojure.repl]]
  [:use [webapp.framework.server.db-helper]]
  [:use [webapp.framework.server.globals]]
  [:use [webapp.framework.server.neo4j-helper]]

  (:use [webapp-config.settings])
  (:use [overtone.at-at])
  (:import [java.util.UUID])
  (:import [java.util TimerTask Timer])
)











(defn test-server-call
  [ params-passed-in ]
  ;----------------------------------------------------------------
  {:value "Return this to the client"}
  )



(defn main-init []
  {:value "do nothing"}
  )





(defn setup-schema
  []
  ;----------------------------------------------------------------
  (do
    (println "Setting up schema")
    (if (= 0 (count (neo4j "MATCH (n:Table) RETURN n" {} "n")))

      (neo4j "CREATE (new_record:Table) RETURN new_record;"))

    {:value "Return this to the client"}
    ))


