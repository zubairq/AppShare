(ns webapp.client.links
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [webapp.framework.client.coreclient   :as  c  :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.framework.client.system-globals  :only  [app-state
                                                    reset-app-state
                                                    ui-watchers
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))





(defn  ^:export confirmLink [uuid-code]
  (go
   (let [ confirm-sender-code-result (c/remote   confirm-user-email-remote-fn
                                                 {
                                                  :sender-code   uuid-code
                                                  })]
     (cond
      (:error confirm-sender-code-result)
      (let [ confirm-receiver-code-result (c/remote "confirm-receiver-code"
                           {
                            :receiver-code   uuid-code
                            })]
        (cond
         (:error confirm-receiver-code-result)
         (.write js/document (:error confirm-receiver-code-result))

         :else
         (.write js/document (str "Your email address has been confirmed with connectToUs. "
                                  "See your connections at <a href='http://connecttous.co'>connectToUs.co</a>"))
         )
        )

      :else
      (.write js/document (str "Your email address has been confirmed. Make your company more visible using "
                               "<a href='http://connecttous.co'>connectToUs.co</a>"))
      )
     )
   )
  []
  )
