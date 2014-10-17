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
  [:use [webapp.server.person-helper]]
;  [:use [webapp.ignore.test-data]]


  (:use [webapp-config.settings])
  (:use [overtone.at-at])
  (:import [java.util.UUID])
  (:import [java.util TimerTask Timer])
)








(defn process-send-confirmation-email
  [ confirm-email-record ]
;----------------------------------------------------------------
  (if confirm-email-record

    (let
      [
       email-confirmation-code    (uuid-str)
       ]
      (do

        (neo4j "match n where id(n)={id}
               remove n:AskToConfirmEmail
               set n:ConfirmationEmailSent,
               n.confirm_sender_code = {confirm_sender_code}
               return n"
               {
                :id                     (:neo-id confirm-email-record)
                :confirm_sender_code    email-confirmation-code
                } "n")

        (send-email
         :message      (str "ConnectToUs.co - "
                            "Please confirm that you wish to join ConnectToUs "
                            "by clicking on the following link: "
                            "\r\n\r\n"
                            "http://" *web-server* "/*" email-confirmation-code)

         :subject      (str "ConnectToUs.co - "
                            "Please confirm you wish to join ConnectToUs "
                            (:to_email confirm-email-record))


         :from-email   "contact@connecttous.co"
         :from-name    "ConnectToUs.co"
         :to-email     (:from_email confirm-email-record)
         :to-name      (:from_email  confirm-email-record))

        ))))














(defn submit-email
  [{:keys [from-email]}]
  ;----------------------------------------------------------------

    (let [
          endorsement-id    (uuid-str)
          web-record        (first (neo4j "create  (n:AskToConfirmEmail
                                          {
                                          endorsement_id:       {endorsement_id},
                                          from_email:           {from_email},
                                          timestamp:            {timestamp}
                                          }) return n"
                                          {
                                           :endorsement_id  endorsement-id
                                           :from_email      from-email
                                           :timestamp       "timestamp"
                                           }
                                          "n"))
          ]
      (do
        (println (str "submit-email called: " web-record))
        {:value web-record}  )))




;(submit-email  {:from-email "dsdsd@fddffd"})




(defn main-init []
  {:value "do nothing"}
  )





(defn confirm-user-email-remote-fn
  [{:keys [sender-code]}]
  ;----------------------------------------------------------------

  (let [n   (neo4j "match (n:ConfirmationEmailSent)
                   where n.confirm_sender_code = {confirm_sender_code}
                   return n"
                   {
                    :confirm_sender_code  sender-code
                    } "n")]
    (if (= (count n) 0)
      {:error "Session doesn't exist"}

      (do
        (let [request (neo4j-1 "match n where
               n.confirm_sender_code = {sender_code}

               remove n:ConfirmationEmailSent
               set n:EmailConfirmed
               return n"
               {
                :sender_code  sender-code
                } "n")]
          (do
            (webapp.server.person-helper/create-person-from-email
             :from-email   (get request :from_email)
             ))

        {:value "Session exists"}
        )))))






(defn email-confirmed
  [{:keys [endorsement-id]}]
  ;----------------------------------------------------------------

  (let [n   (neo4j "match
                     n
                   where
                     n.endorsement_id = {endorsement_id}
                   and
                     n:EmailConfirmed
                   return
                     n"
                   {
                    :endorsement_id  endorsement-id
                    } "n")]
    (if (= (count n) 0)
      {:value false}
      {:value true}
      )))







;----------------------------------------------------------------
(defn get-top-companies []
  ;----------------------------------------------------------------
  (neo4j "match
         (n:Company)-[:WORKS_FOR]-(w:Person)
         return
         n.web_address as company,
         count(w) as inbound
         order by inbound desc
         limit 100"
         {}
         ["company" "inbound"]))


;(get-top-companies )




;----------------------------------------------------------------
(defn get-latest-endorsements []
  ;----------------------------------------------------------------
  (neo4j "match
           (c)-[:WORKS_FOR]-(x)-[r:CONNECT]->(y)-[:WORKS_FOR]-(c2)
         return
           c.web_address as from,
           r.accepted_timestamp as when ,
           c2.web_address as to
         order by
           r.accepted_timestamp desc
         limit
           100"
         {}
         ["from" "to" "when"]))
;(get-latest-endorsements {})



;----------------------------------------------------------------
(defn get-company-details

  [{:keys [company-url]}]
  ;----------------------------------------------------------------

(neo4j "match
         (n:Company)-[:WORKS_FOR]-(w:Person)-[:CONNECT]-someone
         where
         n.web_address={company_name}
         return
         n.web_address        as company,
         count(someone) as inbound
         order by inbound desc
         limit 10"
         {:company_name company-url}
         ["inbound" "company" ]))


;(get-company-details {:company-url "apple.com"})



; read the reasoned schemer
; does core.logic work in clojurescript?



(defn check-messages
  []
  ;----------------------------------------------------------------

  (let [messages-waiting (neo4j "match (n:AskToConfirmEmail) return n" {} "n")]
    (println (str "AskToConfirmEmail: " messages-waiting))
    (dorun (map process-send-confirmation-email  messages-waiting)))


  {:value "Return this to the client"}
  )





(def my-pool (mk-pool))
(stop-and-reset-pool! my-pool)
(def a (atom 0))

(defn check-timer []
  (do
    (future
       (every 5000
              #(do
                 (swap! a inc)
                 (check-messages)
                 (println  "Check timer: "@a)
                 )

              my-pool))
    "********* Loaded background task **********"))


(defn main-init []
  (do
    (check-timer)
    ;(start-conns)
    ))



