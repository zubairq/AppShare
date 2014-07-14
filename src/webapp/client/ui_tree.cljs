(ns webapp.client.ui-tree
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   [ankha.core       :as ankha]
   [webapp.client.timers]
   )
  (:use
   [webapp.client.ui-helpers                :only  [validate-email
                                                    validate-full-name
                                                    validate-endorsement
                                                     ]]
   [webapp.framework.client.coreclient      :only  [log remote
                                                    when-ui-path-equals-fn
                                                    when-ui-value-changes-fn
                                                    when-ui-property-equals-in-record
                                                    amend-record]]
   [webapp.framework.client.system-globals  :only  [app-state   playback-app-state
                                                    playback-controls-state
                                                    data-state
                                                    update-data
                                                    update-ui
                                                    get-in-tree
                                                    ui-watchers
                                                    data-tree!
                                                    data-tree
                                                    ]]
   [clojure.string :only [blank?]]
   )

   (:require-macros
    [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient :only  [when-ui-value-changes
                                               when-ui-path-equals
                                               ns-coils
                                               ui-tree ui-tree!
                                               ]])

  )
(ns-coils 'webapp.client.ui-tree)
















(when-ui-path-equals  [:ui
                         :request
                           :from-email
                             :mode     ]    "validate"

    (cond

      (validate-email (ui-tree [:ui :request :from-email :value]))
        (ui-tree! [:ui :request :from-email :error] "")

      :else
        (ui-tree! [:ui :request :from-email :error] "Invalid email")
    ))












(when-ui-path-equals  [:ui :request :to-email :mode]     "validate"

   (if (validate-email (ui-tree [:ui :request :to-email :value]))
     (ui-tree! [:ui :request :to-email :error] "")
     (ui-tree! [:ui :request :to-email :error] "Invalid email")
     ))





(when-ui-value-changes [:ui :request :to-email :value]

                       (if (= (ui-tree [:ui :request :to-email :mode]) "validate")
                         (if (validate-email (ui-tree [:ui :request :to-email :value]))
                           (ui-tree! [:ui :request :to-email :error] "")
                           (ui-tree! [:ui :request :to-email :error] "Invalid email")
                           )))








(when-ui-path-equals [:ui :request :submit :value]     true

   (go
     (ui-tree! [:ui :request :submit :message] "Submitted")

     (data-tree! [:submit :request :from-email]  (ui-tree [:ui :request :from-email :value]))
     (data-tree! [:submit :request :to-email]    (ui-tree [:ui :request :to-email :value]))
     (data-tree! [:submit :status]               "Submitted")

     (let [ resp (<! (remote "request-endorsement"
             {
              :from-email     (get-in @data-state [:submit :request :from-email])
              :to-email       (get-in @data-state [:submit :request :to-email])
              }))]

         (data-tree! [:submit :request :endorsement-id]  (-> resp :value :endorsement_id))
       )))











(when-ui-property-equals-in-record  [:ui
                                       :companies
                                         :values  ]  :clicked    true

  (fn [ui records]
    (let [r (first records)]
    ;(js/alert (str "record:" r))
      (update-ui  ui
                   [:ui
                      :companies
                        :values   ]

                           (amend-record
                              (into [] (get-in-tree ui [:ui :companies :values]))
                                 "company"
                                 (get r "company")
                                 (fn[z] (merge z {:clicked false}))))

      (update-ui ui [:ui :tab-browser ] "company")
      (update-ui ui [:ui :company-details :company-url] (get r "company"))

)))





(when-ui-value-changes [:ui :company-details :company-url]

   (go
    (ui-tree!  [:ui  :company-details   :skills  ] nil)
     (let [ company-name (<! (remote "get-company-details"
             {
              :company-url    (data-tree [:ui :company-details :company-url])
              }))]

       (data-tree! [:company-details]  company-name)
       )))



(when-ui-path-equals  [:ui   :company-details   :clicked]    true

  (fn [ui]
      (update-ui  ui  [:ui  :company-details   :clicked  ] false)
      (update-ui  ui  [:ui  :tab-browser    ] "top companies")
))








(when-ui-value-changes [:ui :request :from-email :value]

                       (if (= (ui-tree [:ui :request :from-email :mode]) "validate")

                         (if (validate-email  (ui-tree [:ui :request :from-email :value]))

                           (ui-tree! [:ui :request :from-email :error] "")
                           (ui-tree! [:ui :request :from-email :error] "Invalid email")
                           )))

