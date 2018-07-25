(ns simple.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(def app
  (api
    {:swagger {:ui "/"}}

    (GET "/exchanges" []
         :query-params [date :- Long]
         (ok
           (if (< date 0)
             {}
             (let [change (/ (Integer/parseInt (str (last (str (hash (str date)))))) 10)]
               {"GBP" 1
                "USD" (+ 1.4 change)
                "EUR" (+ 1.2 change)
                "AUD" (+ 1.7 change)}))))
    (context "/api" []

      (GET "/plus" []
        :query-params [x :- Long, y :- Long]
        (ok {:result (+ x y)}))
      (GET "/minus" []
        :query-params [x :- Long, y :- Long]
        (ok {:result (- x y)}))



             )))
