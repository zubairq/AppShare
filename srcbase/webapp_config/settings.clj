(ns webapp-config.settings)






(defonce ^:dynamic *mandrill-api-key* "enter_api_key_for_mandrill_here")

(defonce ^:dynamic *record-pointer-locally* false)

(defonce ^:dynamic *email-debug-mode* true)

(defonce ^:dynamic *environment* "base")

(defonce ^:dynamic *web-server* "127.0.0.1:3000")

(defonce ^:dynamic *database-server* "127.0.0.1")

(defonce ^:dynamic *database-user* "postgres")

(defonce ^:dynamic *database-password* "manager")

(defonce ^:dynamic *database-name* "webdb")

(defonce ^:dynamic *sql-encryption-password* "animal")

(defonce ^:dynamic *show-code* true)

(defonce ^:dynamic *main-page* "main.html")

(defonce ^:dynamic *neo4j-server* "localhost")

(defonce ^:dynamic *neo4j-port* "7474")
