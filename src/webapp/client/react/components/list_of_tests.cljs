(ns webapp.client.react.components.list-of-tests
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.client.react.components.list-of-tests)





(c/defn-ui-component     component-list-of-tests   [tests]
  {}

  (c/div nil
         (c/add-many
           (map
                    #(c/div nil
                           (str (get %1 :name) ", "
                                (get %1 :questions_answered_count)))
                    (get tests :values))
                )
         ""))
