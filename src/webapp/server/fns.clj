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





(defn add-row
  []
  ;----------------------------------------------------------------
  (do
    (neo4j "match (r:Rows) set r.row_count = r.row_count + 1 return r")
    (neo4j "match (r:Rows) create (r)-[:ROW]->(row:Row {id: r.row_count}) return r")
    (println "row added")
    {:value "row added"}
    ))


(defn get-rows
  []
  ;----------------------------------------------------------------
  (let [rows  (neo4j "match (r:Rows)-[:ROW]->(rows:Row) return rows" {} "rows")]
    {:rows rows}
    ))


(defn setup-schema
  []
  ;----------------------------------------------------------------
  (do
    (println "Setting up schema")

    (if (= 0 (count (neo4j "MATCH (n:Table) RETURN n" {} "n")))

      (do
        (println "Creating schema")
        (let [rows
              (neo4j "CREATE
                     (new_table:Table),
                     (new_rows:Rows {row_count: 1}),
                     (row:Row {id: 1}),
                     (column:Column {id: 1, value: ''}),
                     (new_table)-[:REL]->(new_rows),
                     (new_rows)-[:ROW]->(row),
                     (row)-[:COLUMN]->(column)
                     RETURN
                     row;")]
          (get-rows)))

      (get-rows))))
