;; injecting some functions into clojure.core that are useful during development.
;; It will add >pprint, >inspect-table, >inspect-tree
;; For debugging spyscope.core is quite handy
(require '[vinyasa.inject :as inject]
         'spyscope.core)

(inject/in [vinyasa.inject :refer [inject [in inject-in]]]

           clojure.core
           [vinyasa.reflection .> .? .* .% .%> .& .>ns .>var]

           clojure.core >
           [clojure.pprint pprint print-table]
           [clojure.inspector inspect-table inspect-tree]
           [clojure.tools.namespace.repl refresh-all])
(ns user
  (:require
    [clojure.tools.namespace.repl :refer (refresh set-refresh-dirs)]
    midje.repl
    [com.iggroup.wt.testservice.main :as main]
    com.iggroup.wt.testservice.routes
    com.iggroup.wt.testservice.util
    current-env
    [com.iggroup.wt.testservice.component-tests :as ctest]
    com.iggroup.wt.testservice.core-test
    [dynapath.dynamic-classpath :as dc]
    [cemerick.pomegranate :as pomegranate])
  (:import (java.net URLClassLoader)))

(comment
  "If there is a compilation issue and this namespace is not available run:"
  (clojure.tools.namespace.repl/refresh-all) or (>refresh-all)
  "To add a dependency to the running application run:"
  (user/add-dependencies '[[com.iggroup.wt/db-util-clj "1.0.0"]]))

(set-refresh-dirs
  "src/main/clojure"
  "src/test/clojure"
  "src/dev"
  "src/test/ctest")

(defn start
  "Starts the current development system."
  []
  (let [new-system (reset! main/system
                           (main/system-start
                             (if (= :dev @current-env/env)
                               (com.iggroup.wt.testservice.util/app-properties ["default.properties.clj" "properties.clj"])
                               (ctest/ctest-props))))]
    (main/replace-http-api! new-system)))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (when @main/system
    (main/system-stop @main/system))
  (reset! main/system nil))

(defn go
  "Initializes the current development system and starts it running."
  []
  (start)
  :ok)

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn autotest []
  (midje.repl/autotest :dirs "src/main/clojure" "src/test/clojure"))

(defn stop-autotest []
  (midje.repl/autotest :stop))

(defn ->dev
  "Restart the system with the dev properties. Any restart/reset afterwards will use the dev properties"
  []
  (reset! current-env/env :dev)
  (stop)
  (start))

(defn ->ctest
  "Restart the system with the component test properties. Any restart/reset afterwards will use the ctest properties"
  []
  (reset! current-env/env :ctest)
  (stop)
  (start))

(defn add-dependencies
  "Coordinates should be a vector as '[[com.iggroup.wt/db-util-clj \"1.0.0\"] [com.iggroup.wt/someother \"2.0.0\"]]"
  [coordinates]
  (pomegranate/add-dependencies :coordinates coordinates
                                :local-repo "C:\\dev\\maven-repo"
                                :repositories {"central"    "http://repo1.maven.org/maven2/"
                                               "clojars"    "http://clojars.org/repo"}))

(defn call-method
  [obj method-name & args]
  (let [m (first (filter (fn [x] (.. x getName (equals method-name)))
                         (.. obj getClass getDeclaredMethods)))]
    (. m (setAccessible true))
    (. m (invoke obj (into-array Object args)))))


(let [base-url-classloader (assoc dc/base-readable-addable-classpath
                             :classpath-urls #(seq (.getURLs ^URLClassLoader %)))]
  (extend org.apache.catalina.loader.WebappClassLoader
    dc/DynamicClasspath
    (assoc base-url-classloader
      :add-classpath-url (fn [^org.apache.catalina.loader.WebappClassLoader cl url]
                           (call-method cl "addURL" url)))))