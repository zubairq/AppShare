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
    [cljs.core.async.macros :refer [go]]))

(c/ns-coils 'webapp.client.actions)



(c/==data  [:actions :add-row] true
           (do
             (c/server-call
              (c/remote "add-row")
              nil
              )
             )
           )
