(ns webapp.client.init
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]]
   [om-sync.core     :as async]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   [webapp.client.timers]
   [webapp.client.data-tree]
   [webapp.client.ui-tree]
   )
  (:use
   [webapp.client.ui-helpers                :only  [validate-email validate-full-name  validate-endorsement]]
   [webapp.client.helper                    :only  [when-path-equals when-value-changes]]
   [webapp.framework.client.coreclient      :only  [log remote]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
                                                    playback-controls-state
                                                    reset-app-state ui-watchers
                                                    playbackmode
                                                    data-watchers
                                                    data-state
                                                    update-data
                                                    set-ab-tests
                                                    set-ab-goals
                                                    ]]
   [webapp.client.react.views.main                    :only   [main-view]]
   [clojure.string :only [blank?]]
   )
   (:require-macros
    [cljs.core.async.macros :refer [go]]))









(defn  ^:export setup []

  (reset!
   app-state

   (assoc-in
    @app-state [:ui]
    {:request {
               :from-full-name       {:label "Your full name"      :placeholder "John smith"         :value ""  :mode "empty"}
               :from-email           {:label "Your company email"  :placeholder "john@microsoft.com" :value ""  :mode "empty"}

               :to-full-name         {:label "Their full name"     :placeholder "Pete Austin"        :value ""  :mode "empty"}
               :to-email             {:label "Their email"         :placeholder "pete@ibm.com"       :value ""  :mode "empty"}

               :endorsement          {:label "Endorsement"         :placeholder "marketing"          :value ""  :mode "empty"}
               :submit               {:value false}
               }

     :tab         "browser"
     :tab-browser "latest endorsements"
     :login {

             :login-email {}
             }


     }))


  (reset! data-state {
                      :submit {}
                      })



  (if js/company_url
    (do
      (reset! app-state
       (assoc-in
        @app-state [:ui :company-details :company-url]
        (str js/company_url)))

      (reset! app-state
       (assoc-in
        @app-state [:ui  :company-details   :skills]
        nil))

      (reset! app-state
       (assoc-in
        @app-state [:ui :tab-browser]
        "company"))


      (go
       (let [ l (<! (remote "get-company-details"
                            {
                             :company-url    (str js/company_url)
                             }))]

         ;(log (pr-str l))
         (reset! data-state
                 (assoc-in
                  @data-state [:company-details]
                  l))


        (reset! app-state
         (assoc-in
          @app-state [:ui :company-details :skills]
               (get-in @data-state [:company-details])))

         ))

      )
    )



  (set-ab-tests {
                 "graph type"
                 {
                    :path [:ui :graph-ab-test]
                    :choices    [
                                 {:name "SVG" :percentage 90}
                                 {:name "text" :percentage 10}
                                 ]
                  }

                 })




  )






(defn ^:export main [app owner]
  (om/build  main-view  app))


