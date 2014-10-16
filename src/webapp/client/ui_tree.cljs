(ns webapp.client.ui-tree
  (:require
   [goog.net.cookies                     :as cookie]
   [om.core                              :as om    :include-macros true]
   [om.dom                               :as dom   :include-macros true]
   [webapp.framework.client.coreclient   :as c     :include-macros true]
   [clojure.data                         :as data]
   [clojure.string                       :as string]
   [ankha.core                           :as ankha]
   [webapp.client.timers]
   [cljs.core.async                      :refer   [put! chan <! pub timeout]]
   [om-sync.core                         :as async]
   )
  (:use
   [webapp.client.ui-helpers                :only  [validate-email]])

  (:require-macros
   [cljs.core.async.macros :refer [go]])

  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils
                                               sql
                                               log
                                               neo4j
                                               defn-ui-component
                                               a
                                               div
                                               write-ui  ==ui  -->ui  watch-ui <--ui
                                               <--data -->data
                                               remote
                                               ]]
   )

  )

(ns-coils 'webapp.client.ui-tree)
















(==ui  [:ui  :request  :from-email  :mode]   "validate"

    (cond

      (validate-email (<--ui [:ui :request :from-email :value]))
        (-->ui [:ui :request :from-email :error] "")

      :else
        (-->ui [:ui :request :from-email :error] "Invalid email")
    ))











(==ui [:ui :request :submit :value]     true

    (do
     (-->ui [:ui :request :submit :message] "Submitted")

     (-->data [:submit :request :from-email]  (<--ui [:ui :request :from-email :value]))
     (-->data [:submit :status]               "Submitted")

     (go
      (let [ resp (remote    submit-email
             {
              :from-email     (<--data [:submit :request :from-email])
              })]

         (cond
          (resp :error)
            (-->data [:submit :response]  (pr-str resp))

          :else
           (-->data [:submit :request :endorsement-id]  (-> resp :value :endorsement_id))
       )))))











(c/when-ui-property-equals-in-record  [:ui
                                       :companies
                                         :values  ]  :clicked    true

  (fn [ui records]
    (let [r (first records)]
    ;(js/alert (str "record:" r))
      (c/update-ui  ui
                   [:ui
                      :companies
                        :values   ]

                           (c/amend-record
                              (into [] (c/get-in-tree ui [:ui :companies :values]))
                                 "company"
                                 (get r "company")
                                 (fn[z] (merge z {:clicked false}))))

      (c/update-ui ui [:ui :tab-browser ] "company")
      (c/update-ui ui [:ui :company-details :company-url] (get r "company"))

)))





(watch-ui [:ui :company-details :company-url]

   (go
    (-->ui  [:ui  :company-details   :skills  ] nil)
     (let [ company-name (remote "get-company-details"
             {
              :company-url    (<--data [:ui :company-details :company-url])
              })]

       (-->data [:company-details]  company-name)
       )))



(==ui  [:ui   :company-details   :clicked]    true

      (-->ui  [:ui  :company-details   :clicked  ] false)
      (-->ui  [:ui  :tab-browser    ] "top companies"))








(watch-ui [:ui :request :from-email :value]

          (if (= (<--ui [:ui :request :from-email :mode]) "validate")

            (if (validate-email  (<--ui [:ui :request :from-email :value]))

              (-->ui [:ui :request :from-email :error] "")
              (-->ui [:ui :request :from-email :error] "Invalid email")
              )))



(watch-ui [:ui :request :from-email :value]

          (if
            (validate-email  (<--ui [:ui :request :from-email :value]))

            (-->ui [:ui :request :details-valid] true)
            ))

