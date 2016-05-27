(ns com.iggroup.wt.testservice.util
  (:require [clojure.walk]
            [flatland.useful.map :as useful]
            clojure.string)
  (:import [java.io PushbackReader]))

(defn remove-context
  "Removes the deployed servlet context from a URI when running as a
   deployed web application"
  [handler]
  (fn [request]
    (if-let [context (:servlet-context-path request)]
      (let [uri (:uri request)]
        (if (.startsWith uri context)
          (handler (assoc request :uri
                                  (.substring uri (.length context))))
          (handler request)))
      (handler request))))

(defn wrap-with-additional-keys-in-req
  "Adds to all requests the key value pairs"
  [handler & additional-keys-in-req]
  (fn [req]
    (handler (apply assoc req additional-keys-in-req))))

(defn- system-properties []
  (clojure.walk/keywordize-keys (into {} (System/getProperties))))

(defn file [filename]
  (.getResource (.getContextClassLoader (Thread/currentThread)) filename))

(defmulti file-properties type)

(defmethod file-properties java.io.File [filename]
  (read-string (slurp filename)))

(defmethod file-properties java.net.URL [url]
  (with-open [r (clojure.java.io/reader url)]
    (read (PushbackReader. r))))

(defmethod file-properties String [path]
  (if-let [url (.getResource (.getContextClassLoader (Thread/currentThread)) path)]
    (file-properties url)
    (throw (RuntimeException. (str "File not found:" path)))))

(defn- lookup-replacement [properties]
  (fn [groups]
    (let [thing-to-replace (second groups)]
      (str (get properties (keyword thing-to-replace) thing-to-replace)))))

(defn- replace-placeholders [properties value]
  (if-not (string? value)
    value
    (clojure.string/replace value #"\{([\w\.-]+)\}" (lookup-replacement properties))))

(defn- resolve-placeholders [properties]
  (clojure.walk/postwalk (partial replace-placeholders properties) properties))

(defn- to-number-if-possible [str]
  (if (= String (type str))
    (cond (re-seq #"^[-+]?\d*[\.,]\d*$" str)
          (Double/parseDouble (clojure.string/replace str #"," "."))
          (re-seq #"^[-+]?\d+$" str)
          (Integer/parseInt (clojure.string/replace str #"\+" ""))
          :else str)
    str))

(defn merge-and-resolve [& props]
  (clojure.walk/postwalk to-number-if-possible (resolve-placeholders (apply useful/merge-in props))))

(defn app-properties [filenames]
  (apply merge-and-resolve (conj (mapv file-properties filenames) (system-properties))))

(defn ig-standard-files []
  (keep file ["default.properties.clj"
              "properties.clj"]))

(def ig-standard (comp app-properties ig-standard-files))
