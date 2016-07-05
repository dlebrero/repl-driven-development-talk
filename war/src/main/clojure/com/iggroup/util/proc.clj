(ns com.iggroup.util.proc
  (:require [clojure.java.jdbc :as jdbc]
            clojure.set)
  (:import (java.sql ResultSet CallableStatement Connection)))

(def ^{:private true
       :doc "Copied from clojure.java.jdbc as it is private"}
result-set-type
  {:forward-only       ResultSet/TYPE_FORWARD_ONLY
   :scroll-insensitive ResultSet/TYPE_SCROLL_INSENSITIVE
   :scroll-sensitive   ResultSet/TYPE_SCROLL_SENSITIVE})

(def ^{:private true
       :doc "Copied from clojure.java.jdbc as it is private"}
result-set-concurrency
  {:read-only ResultSet/CONCUR_READ_ONLY
   :updatable ResultSet/CONCUR_UPDATABLE})

(def ^{:private true
       :doc "Copied from clojure.java.jdbc as it is private"}
result-set-holdability
  {:hold ResultSet/HOLD_CURSORS_OVER_COMMIT
   :close ResultSet/CLOSE_CURSORS_AT_COMMIT})

(defn callable-statement
  "Create a callable statement from a connection, a SQL string and an
   optional list of parameters:
     :result-type :forward-only | :scroll-insensitive | :scroll-sensitive
     :concurrency :read-only | :updatable
     :cursors
     :fetch-size n
     :max-rows n"
  [^Connection con ^String sql &
   {:keys [result-type concurrency cursors fetch-size max-rows]}]
  (let [^CallableStatement
  stmt (cond (and result-type concurrency)
             (if cursors
               (.prepareCall con sql
                             (result-type result-set-type)
                             (concurrency result-set-concurrency)
                             (cursors result-set-holdability))
               (.prepareCall con sql
                             (result-type result-set-type)
                             (concurrency result-set-concurrency)))
             :else
             (.prepareCall con sql))]
    (when fetch-size (.setFetchSize stmt fetch-size))
    (when max-rows (.setMaxRows stmt max-rows))
    stmt))


(defn- register-out-params
  "Registers the out parameters to the given statement."
  [^CallableStatement stmt types out-params-indices]
  (doseq [^int idx out-params-indices]
    (if-let [^int type (get types idx)]
      (.registerOutParameter stmt idx type)
      (throw (Exception. (str "SQL Type required for index " idx))))))

(defn- set-in-parameters
  "Add the parameters to the given statement."
  [^CallableStatement stmt types in-params]
  (doseq [[ix value] in-params
          :let [^int type (types ix)]]
    (if (and type (nil? value))
      (.setNull stmt ^int ix type)
      (jdbc/set-parameter value stmt ix))))                      ;Not using type for non-nil values

(defn- get-out-params
  "Returns all the out parameters"
  [^CallableStatement stmt out-params-indices]
  (into {} (map (fn [ix]
                  (let [out-value (.getObject stmt ^int ix)]
                    (if (instance? ResultSet out-value)
                      [ix (apply list (resultset-seq out-value))]
                      [ix (.getObject stmt ^int ix)])))
                out-params-indices)))

(defn db-do-execute-callable-statement
  [^CallableStatement stmt types in-params out-params-indices]
  (do
    (set-in-parameters stmt types in-params)
    (register-out-params stmt types out-params-indices)
    (.execute stmt)
    (get-out-params stmt out-params-indices)))


(defn db-do-callable
  "Executes an SQL callable statement on the open database connection.
  transaction? can be omitted and defaults to true.
  Return a map of out params"
  [db sql types in-params out-params-indices]
  (jdbc/with-db-transaction
    [con db]
    (if (instance? CallableStatement sql)
      (db-do-execute-callable-statement sql types in-params out-params-indices)
      (with-open [^CallableStatement stmt (callable-statement (jdbc/db-find-connection con) sql)]
        (db-do-execute-callable-statement stmt types in-params out-params-indices)))))

(defn- index->nth [params n]
  (zipmap (next (range))
          (map #(nth % n) params)))

(defn- index->type [params]
  (index->nth params 1))

(defn- index->param-name [params]
  (index->nth params 2))

(defn- create-in-params [params param-values]
  (into {} (keep-indexed (fn [i v]
                           (when (#{:in :in-out} (first v))
                             [(inc i) (param-values (nth v 2))]))
                         params)))

(defn- create-out-params-indices [params]
  (keep-indexed (fn [i v]
                  (when (#{:out :in-out :return} (first v))
                    (inc i)))
                params))

(defn call-procedure
  "Returns a function which takes db and the input params for the given procedure and the metadata.
  Output for the returned function is a map of out parmeters where the keys are the parameterized names.
  Args:-
      proc-name - procedure name
      params - Each parameter is seq of input-param-type, data-type and parameterized name.
               input-param-type can be :in or :out or :in-out or :return
               data-type - sql-type"
  [proc-name & params]
  (let [has-return (= :return (ffirst params))
        [call-prefix ?-count] (if has-return
                                ["{ ? = call " (dec (count params))]
                                ["{ call " (count params)])
        sql (str call-prefix proc-name "(" (clojure.string/join "," (repeat ?-count "?")) ")}")
        types (index->type params)
        index-to-param-name (index->param-name params)
        out-params (create-out-params-indices params)]
    (fn [db & {:as param-values}]
      (let [in-params (create-in-params params param-values)
            result (db-do-callable db sql types in-params out-params)]
        (clojure.set/rename-keys result index-to-param-name)))))

(defmacro def-proc [fn-name & params]
  `(def ~fn-name (call-procedure ~@params)))
