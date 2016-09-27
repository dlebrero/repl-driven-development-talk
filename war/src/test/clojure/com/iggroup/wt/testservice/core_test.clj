(ns com.iggroup.wt.testservice.core-test
  (:require [com.iggroup.wt.testservice.core :as core]
            [com.iggroup.wt.testservice.db :as db])
  (:use
    [clojure.test :only [deftest]]
    [midje.sweet]))


(deftest total
  (fact "just valid tx in total"
        #_(let [txs []]
          (db/summary txs) => 0.0)))