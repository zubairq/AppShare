(ns webapp.server.person-helper
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
  [:use [clojure.string :only [split]] ]


  (:use [webapp-config.settings])
  (:use [overtone.at-at])
  (:import [java.util.UUID])
  (:import [java.util TimerTask Timer])
)


(defn get-company-url-from-email [email]
  (second (split email #"@")))


(defn company-exists? [& {:keys [web-address]}]
  (let [x
    (neo4j "MATCH (n:Company)
           where n.web_address={web_address} return n"
           {:web_address  web-address}
           "n")]
    (cond
     (= (count x) 0)
         false
     :else
       true)))


(defn person-exists? [& {:keys [email]}]
  (let [x
    (neo4j "MATCH (n:Person)
           where n.email={email} return n"
           {:email  email}
           "n")]
    (cond
     (= (count x) 0)
         false
     :else
       true)))



(defn create-company [& {:keys [web-address]}]
  (do
  (cond
   (not (company-exists? :web-address web-address))

   (neo4j "create  (n:Company
          {
          web_address:          {web_address}
          }) return n"
          {
           :web_address  web-address
           }
          "n"))))



(defn link-person-to-company [& {:keys [email web-address]}]
  (do
    (neo4j "match
           (person:Person),(company:Company)
           where
           person.email={email} and
           company.web_address={web_address}
           create
           (person)-[:WORKS_FOR {accepted_timestamp: {accepted_timestamp}}]->(company)
           return
           person"
           {
            :email          email
            :web_address    web-address
            :accepted_timestamp (. (java.util.Date.) getTime)
            }
           ["person"])))


(defn create-person [& {:keys [email web-address]}]
  (do
    (cond
     (not (person-exists?   :email       email))

     (do
       (neo4j "create  (n:Person
              {
              email:                {email},
              type:                 'human'
              }) return n"
              {
               :email        email
               }
              "n")
       (create-company  :web-address  web-address)
       (link-person-to-company :email email  :web-address  web-address)


       ))))







(defn endorse2 [& {:keys [from-email]}]
  (let
    [
     from-company      (get-company-url-from-email from-email)
     ]
      (do
        (create-person   :email           from-email
                         :web-address     from-company))))


;(endorse2 :from-email "zubairq@gmail")
