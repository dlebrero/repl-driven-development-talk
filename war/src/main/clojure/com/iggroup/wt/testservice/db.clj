(ns com.iggroup.wt.testservice.db
  (:require clojure.string
            [com.iggroup.wt.testservice.core :as core]
            [com.iggroup.util.dev :as dev]
            [com.iggroup.util.proc :as proc :refer [def-proc]]
            [clj-http.client :as http]
            [clojure.java.jdbc :as jdbc]
            [cheshire.core :as json])
  (:import (java.sql Types)))


(comment
  (set! *print-length* 10)

  (def db "jdbc:postgresql://localhost:5432/dlebrero")

  (jdbc/query db "select 1")

  (user/add-dependencies '[[org.postgresql/postgresql "9.4-1202-jdbc42"]])

  )

(comment

  (dev/find-table db :table "transa%" :schema "public")
  (>print-table
    (dev/find-table db :table "transa%" :schema "public"))

  (>print-table
    (jdbc/query db ["select * from transaction LIMIT 5"]))

  (>print-table
    (dev/find-proc db :name "%trans%" :schema "public"))

  (->> (dev/find-proc db :name "transactions_in_gbp")
       first
       (dev/describe-proc db)
       dev/print-definition)
  )



(comment

  (transactions-in-gbp db :clientid 1)

  (map (fn [c] (transactions-in-gbp db :clientid c))
       (range 1 10))

  (do
    (require '(incanter core charts))
    (->> (mapcat
           #(-> (transactions-in-gbp db :clientid %)
                :return-value)
           (range 20))
         (map (comp #(BigDecimal. %) :amount))
         incanter.charts/histogram
         incanter.core/view))

  )

(defn type= [expected]
  (fn [tx]
    (= expected (:type tx))))

(defn summary [txs]
  (->> txs
       ;(filter (comp neg? :amount))
       ;(remove (type= 101))
       (map :amount)
       (reduce +)))

(comment
  (user/autotest))

(comment
  (defn total-transactions [db client]
    (-> (transactions-in-gbp db :clientid client)
        :return-value
        summary)))