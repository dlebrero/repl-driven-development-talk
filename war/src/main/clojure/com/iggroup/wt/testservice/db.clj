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

  (def db "jdbc:postgresql://localhost:5431/dlebrero")

  (jdbc/query db "select 1")

  (user/add-dependencies '[[org.postgresql/postgresql "9.4-1202-jdbc42"]])
  )

(comment

  (dev/find-table db :table "transa%" :schema "public")
  (>print-table
    (dev/find-table db :table "transa%" :schema "public"))

  (jdbc/query db ["select * from transaction limit 5"])

  (>print-table (take 3 (jdbc/query db ["select * from transaction"])))

  (jdbc/query db ["select * from transaction where client = ?" 1])
  )

(comment

  (>print-table (by-client db 1))

  (http/get "http://localhost:3000/exchanges")

  (do
    (require '(incanter core charts))

    (->> p
         clojure.core.matrix.dataset/dataset
         (incanter.charts/time-series-plot :time :EUR :data)
         incanter.core/view))
  )

(comment

  (dev/find-proc db :name "%trans%")
  (>print-table (dev/find-proc db :name "%trans%"))
  (>print-table (dev/find-proc db :name "%trans%" :schema "public"))

  (dev/find-proc db :name "transactions_in_gbp")

  (->> (dev/find-proc db :name "transactions_in_gbp")
       first
       (dev/describe-proc db)
       dev/print-definition)
  )

(comment
  (transactions-in-gbp db :clientid 1)
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