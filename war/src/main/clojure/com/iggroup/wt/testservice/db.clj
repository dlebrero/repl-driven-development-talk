(ns com.iggroup.wt.testservice.db
  (:require clojure.string
            [com.iggroup.wt.testservice.core :as core]))



(comment
  (get-watchlist-info db :account-id account)
  (def wls (get-watchlist-info db :account-id account))
  (class wls)
  (keys wls)
  (:csr-info wls)
  (>print-table (:csr-info wls))

  (:csr-views (get-watchlist-instruments db :account-id account :view-id "2888109"))

  (get-watchlist-info db :account-id "i am not a valid account")
  (get-watchlist-instruments db :account-id account :view-id "I do not exist")
  (get-watchlist-instruments db :account-id "an invalid account" :view-id "2888109")

  (>print-table (map #(select-keys % [:my_views_id :market_count :editable]) (:csr-info wls)))

  (mapv #(select-keys % [:my_views_id :market_count :editable]) (:csr-info wls))
  )

(defn has-markets? [wl]
  (> 0 (:market_count wl)))

(defn parse-wls [wls]
  )

(comment
  (user/autotest)

  (parse-wls
      (:csr-info
        (get-watchlist-info db :account-id account))))

(comment
  (defn get-wls-epics [db account-id]
    (->>
      (get-watchlist-info db :account-id account-id)
      :csr-info
      parse-wls
      (map (partial get-watchlist-instruments db :account-id account-id :view-id))
      (mapcat :csr-views)
      (map :epic))))