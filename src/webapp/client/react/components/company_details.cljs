(ns webapp.client.react.components.company-details
  (:require
   [om.core                              :as om :include-macros true]
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.client.react.components.company-details)









;---------------------------------------------------------
(c/defn-ui-component   company-details2    [company-details]
  {:absolute-path [:ui :company-details]}
;---------------------------------------------------------
  (c/div
   {:style {:height "100%" :width "100%"}}

   (c/div {:style {:padding-bottom "0px"}}
            (str (-> company-details :company-url)))
   (if (-> company-details :skills )
     (do
       (c/div {:style {:padding-bottom "20px"}} "Skills2")
       (apply om.dom/div nil
              (map
               (fn[skill]
                 (c/div {:style {:padding-left "20px"}}
                          (get skill "skill") " - " (get skill "skill_count")
                          ))
               (-> company-details :skills )
               ))
       )

     ;else
     (c/div {:style {:padding-bottom "20px"}} "Loading skills..."))

   (c/div {:style {:padding-bottom "20px"}} "")

   (c/pre nil
            (str "<iframe id='widget_" (str (-> company-details :company-url))
                 "' width='100%' height='100%' "
                 "src='http://connecttous.co/connecttous/connecttous.html"
                 "?company_url="
                 (str (-> company-details :company-url))
                 "'></iframe>"))))
