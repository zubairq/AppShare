(ns webapp.client.ui-helpers
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   )
  (:use
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    playback-app-state
                                                    playback-controls-state
                                                    reset-app-state
                                                    ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    remove-debug-event
                                                    ]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))







(defn validate-email [email]
  (if (pos? (.indexOf (str email) "@"))
    true
    ))



