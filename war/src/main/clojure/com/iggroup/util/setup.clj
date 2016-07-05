(ns com.iggroup.util.setup
  (:require [clojure.java.jdbc :as jdbc]))

(comment

  (def db "jdbc:postgresql://localhost:5432/dlebrero")

  (jdbc/execute! db ["CREATE TABLE transaction (
                          id            serial primary key,
                          client        INT,
                          type          INT,
                          currency      VARCHAR(3),
                          amount        FLOAT,
                          timestamp     bigint);"])

  (jdbc/execute! db ["CREATE TABLE exchange (
                          currency      VARCHAR(3),
                          change        FLOAT);"])

  (jdbc/execute! db "delete from transaction")

  (do
    (jdbc/insert! db "transaction" {:client 1 :type 100 :currency "GBP" :amount 23 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 1 :type 100 :currency "GBP" :amount -5 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 1 :type 101 :currency "EUR" :amount 10 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 1 :type 101 :currency "EUR" :amount 10 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 2 :type 101 :currency "EUR" :amount 103.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 2 :type 101 :currency "USD" :amount 223.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 3 :type 101 :currency "AUD" :amount 1043.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 4 :type 101 :currency "GBP" :amount 523.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 5 :type 101 :currency "EUR" :amount 1503.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 5 :type 101 :currency "GBP" :amount 3.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 5 :type 101 :currency "EUR" :amount 3.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 6 :type 101 :currency "EUR" :amount 6.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 7 :type 101 :currency "EUR" :amount 453 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 7 :type 101 :currency "EUR" :amount 22 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 7 :type 101 :currency "EUR" :amount 1 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "AUD" :amount 17.3 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "AUD" :amount -117.3 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "AUD" :amount -1217.3 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "AUD" :amount -10.3 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "AUD" :amount -217.3 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 8 :type 101 :currency "USD" :amount 34 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 9 :type 101 :currency "GBP" :amount 23.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 9 :type 101 :currency "USD" :amount 100.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 10 :type 101 :currency "EUR" :amount 20.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 11 :type 101 :currency "EUR" :amount 100.4 :timestamp (System/currentTimeMillis)})
    (jdbc/insert! db "transaction" {:client 11 :type 101 :currency "EUR" :amount 22.4 :timestamp (System/currentTimeMillis)}))

  (jdbc/insert-multi! db "exchange" [{:currency "EUR" :change 1.2}
                                     {:currency "GBP" :change 1}
                                     {:currency "USD" :change 1.4}
                                     {:currency "AUD" :change 1.7}])

  (jdbc/execute! db (jdbc/drop-table-ddl "transaction"))
  (jdbc/execute! db ["DROP FUNCTION fn_sqltestout()"])

  (jdbc/execute! db ["CREATE OR REPLACE FUNCTION transactions_in_gbp(clientId bigint) RETURNS refcursor AS $$\n
                     DECLARE\n
                     ref refcursor;\n
                     BEGIN\n
                     OPEN ref FOR SELECT t.id,t.type,t.timestamp,t.amount * e.change as amount FROM transaction t,exchange e where t.client = clientId AND t.currency = e.currency;\n
                     RETURN ref;\n
                     END;\n
                     $$ LANGUAGE plpgsql;\n"])

  (jdbc/execute! db ["CREATE OR REPLACE FUNCTION transactions_in_eur(clientId bigint) RETURNS refcursor AS $$\n
                     DECLARE\n
                     ref refcursor;\n
                     BEGIN\n
                     OPEN ref FOR SELECT t.id,t.type,t.timestamp,t.amount * e.change as amount FROM transaction t,exchange e where t.client = clientId AND t.currency = e.currency;\n
                     RETURN ref;\n
                     END;\n
                     $$ LANGUAGE plpgsql;\n"]))