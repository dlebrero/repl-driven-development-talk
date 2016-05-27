(ns com.iggroup.wt.testservice.kfk
  #_(:require [clj-kafka.core :as k]
      [clj-kafka.consumer.zk :as zk])
  #_(:import (kafka.serializer StringDecoder)))

(comment

  (user/add-dependencies '[[clj-kafka "0.3.4"]])

  (def config {"zookeeper.connect"               "url"
               "group.id"                        "clj-kafka.consumer"
               "zookeeper.connection.timeout.ms" "1000"
               "auto.commit.enable"              "false"})

  (def c (zk/consumer config))
  (zk/shutdown c)
  (def ms (zk/messages c "DATA.IG.LEDGER.TRANSACTION"
                       :value-decoder (StringDecoder. nil)))

  (first ms)
  (-> ms first :value)
  (-> ms first :value cheshire.core/parse-string)
  (-> ms first :value cheshire.core/parse-string >inspect-tree)

  (defn parse [msg]
    (cheshire.core/parse-string (:value msg) true))

  (->> ms
       (map parse)
       (map :environment)
       (take 100)
       frequencies)

  (->> ms
       (map parse)
       (map :payload)
       (take 100)
       (map #(contains? % :closeBet))
       frequencies)

  (do
    (require '(incanter core charts))
    (->> ms
         (map parse)
         (map :payload)
         (filter :closeBet)
         (take 100)
         (map (comp #(BigDecimal. %) :amount :closeBet))
         incanter.charts/histogram
         incanter.core/view))

  (->> ms
       (map parse)
       (filter (comp (partial = "LIVE") :environment))
       (map :payload)
       (filter :closeBet)
       (take 100)
       (map (comp #(BigDecimal. %) :amount :closeBet))
       incanter.charts/histogram
       incanter.core/view)

  )
