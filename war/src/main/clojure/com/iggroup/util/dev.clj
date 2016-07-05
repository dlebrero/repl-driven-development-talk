(ns com.iggroup.util.dev
  (:require [clojure.java.jdbc :as j]
            [com.iggroup.util.proc :as proc]
            [superstring.core :as ss]
            clojure.string
            clojure.set
            [clojure.pprint :as p])
  (:import (java.sql Types DatabaseMetaData)))

(defn find-column [db & {:keys [catalog schema table column]}]
  (j/with-db-metadata [^DatabaseMetaData m db]
                      (clojure.java.jdbc/metadata-query (.getColumns m catalog schema table column))))

(defn find-table [db & {:keys [catalog schema table types]}]
  (j/with-db-metadata [^DatabaseMetaData m db]
                      (clojure.java.jdbc/metadata-query (.getTables m catalog schema table types))))

(defn find-proc [db & {:keys [catalog schema name] :or {name "%"}}]
  (map #(select-keys % [:procedure_cat
                        :procedure_schem
                        :procedure_name
                        :remarks
                        :procedure_type
                        :specific_name])
       (j/with-db-metadata [m db]
                           (j/metadata-query (.getProcedures m catalog schema name)))))

(defn describe-proc
  [db proc-spec-or-keyword & more]
  (let [{:keys [procedure_cat
                procedure_schema
                procedure_name]} (if (map? proc-spec-or-keyword)
                                   proc-spec-or-keyword
                                   (clojure.set/rename-keys
                                     (into {} (map vector (cons proc-spec-or-keyword more)))
                                     {:catalog :procedure_cat
                                      :schema  :procedure_schema
                                      :name    :procedure_name}))]
    (assert (and procedure_name) "missing params")
    (j/with-db-metadata [m db]
                        (j/metadata-query
                          (.getProcedureColumns m procedure_cat procedure_schema procedure_name "%")))))

(defn in-out [i]
  (condp == i
    1 :in
    2 :in-out
    4 :out
    5 :return))

(defn postgres-type [type-name]
  (condp = (clojure.string/upper-case type-name)
    "VARCHAR2" 'Types/VARCHAR
    "VARCHAR" 'Types/VARCHAR
    "BPCHAR" 'Types/VARCHAR
    "REFCURSOR" 'Types/OTHER
    "NUMBER" 'Types/NUMBER
    "INT4" 'Types/BIGINT
    "INT8" 'Types/BIGINT
    "TIMESTAMP" 'Types/TIMESTAMP
    "DATE" 'Types/DATE
    "BLOB" 'Types/BLOB))

(defn column-name [name]
  (-> name
      ss/lisp-case
      (ss/chop-prefix "P-" true)
      (ss/chop-prefix "R-" true)
      keyword))

(defn define-proc [c]
  (let [proc-name (:procedure_name (first c))
        proc-cat (:procedure_cat (first c))
        full-name (if (clojure.string/blank? proc-cat)
                    proc-name
                    (str proc-cat "." proc-name))]
    (concat ['def-proc
             (symbol (ss/lisp-case proc-name))
             full-name]
            (map (juxt (comp in-out :column_type)
                       (comp postgres-type :type_name)
                       (comp column-name :column_name))
                 c))))

(def print-definition (comp p/pprint #'define-proc))