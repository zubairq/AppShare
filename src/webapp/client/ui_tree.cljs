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
   )
  (:use
   [webapp.client.ui-helpers                :only  [validate-email]])

   (:require-macros
    [cljs.core.async.macros :refer [go]]))

(c/ns-coils 'webapp.client.ui-tree)
















(c/==ui  [:ui  :request  :from-email  :mode]   "validate"

    (cond

      (validate-email (c/<--ui [:ui :request :from-email :value]))
        (c/-->ui [:ui :request :from-email :error] "")

      :else
        (c/-->ui [:ui :request :from-email :error] "Invalid email")
    ))










(c/==ui  [:ui :request :to-email :mode] "validate"

   (if (validate-email (c/<--ui [:ui :request :to-email :value]))
     (c/-->ui [:ui :request :to-email :error] "")
     (c/-->ui [:ui :request :to-email :error] "Invalid email")
     ))







(c/watch-ui [:ui :request :to-email :value]

                       (if (= (c/<--ui [:ui :request :to-email :mode]) "validate")
                         (if (validate-email (c/<--ui [:ui :request :to-email :value]))
                           (c/-->ui [:ui :request :to-email :error] "")
                           (c/-->ui [:ui :request :to-email :error] "Invalid email")
                           )))








(c/==ui [:ui :request :submit :value]     true

    (do
     (c/-->ui [:ui :request :submit :message] "Submitted")

     (c/-->data [:submit :request :from-email]  (c/<--ui [:ui :request :from-email :value]))
     (c/-->data [:submit :request :to-email]    (c/<--ui [:ui :request :to-email :value]))
     (c/-->data [:submit :status]               "Submitted")

     (go
      (let [ resp (c/remote "request-endorsement"
             {
              :from-email     (c/<--data [:submit :request :from-email])
              :to-email       (c/<--data [:submit :request :to-email])
              })]

         (cond
          (resp :error)
            (c/-->data [:submit :response]  (pr-str resp))

          :else
           (c/-->data [:submit :request :endorsement-id]  (-> resp :value :endorsement_id))
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





(c/watch-ui [:ui :company-details :company-url]

   (go
    (c/-->ui  [:ui  :company-details   :skills  ] nil)
     (let [ company-name (c/remote "get-company-details"
             {
              :company-url    (c/<--data [:ui :company-details :company-url])
              })]

       (c/-->data [:company-details]  company-name)
       )))



(c/==ui  [:ui   :company-details   :clicked]    true

      (c/-->ui  [:ui  :company-details   :clicked  ] false)
      (c/-->ui  [:ui  :tab-browser    ] "top companies"))








(c/watch-ui [:ui :request :from-email :value]

                       (if (= (c/<--ui [:ui :request :from-email :mode]) "validate")

                         (if (validate-email  (c/<--ui [:ui :request :from-email :value]))

                           (c/-->ui [:ui :request :from-email :error] "")
                           (c/-->ui [:ui :request :from-email :error] "Invalid email")
                           )))



(c/watch-ui [:ui :request :from-email :value]

                         (if
                           (and
                            (validate-email  (c/<--ui [:ui :request :from-email :value]))
                            (validate-email  (c/<--ui [:ui :request :to-email :value])))

                           (c/-->ui [:ui :request :details-valid] true)
                           ))

(c/watch-ui [:ui :request :to-email :value]

                         (if
                           (and
                            (validate-email  (c/<--ui [:ui :request :from-email :value]))
                            (validate-email  (c/<--ui [:ui :request :to-email :value])))

                           (c/-->ui [:ui :request :details-valid] true)
                           ))
