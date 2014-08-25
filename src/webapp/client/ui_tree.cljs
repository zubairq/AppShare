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
                                                     ]]
   [webapp.framework.client.coreclient      :only  [log remote
                                                    when-ui-path-equals-fn
                                                    when-ui-value-changes-fn
                                                    get-in-tree
                                                    when-ui-property-equals-in-record
                                                    amend-record
                                                    remove-debug-event

                                                    update-ui]]
   [webapp.framework.client.system-globals  :only  [app-state
                                                    data-state
                                                    update-data
                                                    ui-watchers
                                                    -->data
                                                    <--data
                                                    ]]
   [clojure.string :only [blank?]]
   )

   (:require-macros
    [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient :only  [
                                               ==ui
                                               watch-ui
                                               <--ui
                                               -->ui
                                               ns-coils
                                               ]])

  )
(ns-coils 'webapp.client.ui-tree)
















(==ui  [:ui  :request  :from-email  :mode]   "validate"

    (cond

      (validate-email (<--ui [:ui :request :from-email :value]))
        (-->ui [:ui :request :from-email :error] "")

      :else
        (-->ui [:ui :request :from-email :error] "Invalid email")
    ))










(==ui  [:ui :request :to-email :mode] "validate"

   (if (validate-email (<--ui [:ui :request :to-email :value]))
     (-->ui [:ui :request :to-email :error] "")
     (-->ui [:ui :request :to-email :error] "Invalid email")
     ))







(watch-ui [:ui :request :to-email :value]

                       (if (= (<--ui [:ui :request :to-email :mode]) "validate")
                         (if (validate-email (<--ui [:ui :request :to-email :value]))
                           (-->ui [:ui :request :to-email :error] "")
                           (-->ui [:ui :request :to-email :error] "Invalid email")
                           )))








(==ui [:ui :request :submit :value]     true

   (go
     (-->ui [:ui :request :submit :message] "Submitted")

     (-->data [:submit :request :from-email]  (<--ui [:ui :request :from-email :value]))
     (-->data [:submit :request :to-email]    (<--ui [:ui :request :to-email :value]))
     (-->data [:submit :status]               "Submitted")

     (let [ resp (<! (remote "request-endorsement"
             {
              :from-email     (<--data [:submit :request :from-email])
              :to-email       (<--data [:submit :request :to-email])
              }))]

         (cond
          (resp :error)
            (-->data [:submit :response]  (pr-str resp))

          :else
           (-->data [:submit :request :endorsement-id]  (-> resp :value :endorsement_id))
       ))))











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





(watch-ui [:ui :company-details :company-url]

   (go
    (-->ui  [:ui  :company-details   :skills  ] nil)
     (let [ company-name (<! (remote "get-company-details"
             {
              :company-url    (<--data [:ui :company-details :company-url])
              }))]

       (-->data [:company-details]  company-name)
       )))



(==ui  [:ui   :company-details   :clicked]    true

      (-->ui  [:ui  :company-details   :clicked  ] false)
      (-->ui  [:ui  :tab-browser    ] "top companies")
)








(watch-ui [:ui :request :from-email :value]

                       (if (= (<--ui [:ui :request :from-email :mode]) "validate")

                         (if (validate-email  (<--ui [:ui :request :from-email :value]))

                           (-->ui [:ui :request :from-email :error] "")
                           (-->ui [:ui :request :from-email :error] "Invalid email")
                           )))

