(ns com.iggroup.wt.testservice.http
  (:require [clojure.string :as s]))

(defn ma-ids [mt epics]
  (->>
    (mt :get "/epicsToMarketIds"
        :epics (s/join "," epics))
    vals
    (map :id)))

(defn also-traded [cs ma-id]
  (->>
    (cs :get (str "/markets/" ma-id "/open-positions/also-traded"))
    :markets
    (map :marketId)))

(defn recommend [[mt cs] epics]
  (let [ma-ids (ma-ids mt epics)]
    (mapcat (partial also-traded cs) ma-ids)))

(comment



  )