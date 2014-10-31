(ns webapp.client.react.components.list-of-tests
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.client.react.components.list-of-tests)


(defn data [tests]
      (get tests :values)
      )


(c/defn-ui-component     component-list-of-tests   [tests]
  {}

  (c/map-many

   #(c/container
     (c/inline "150px" (c/text (:name %1) ))
     (c/inline ""      (c/text (:questions_answered_count %1)))
     )

   (data  tests)))
