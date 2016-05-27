(ns com.iggroup.wt.testservice.component-tests
  (:require
            [cheshire.core :as json]
            com.iggroup.wt.testservice.util)
  (:use
    [clojure.test :only [deftest]]
    [compojure.core :only [routes defroutes GET]]
    [midje.sweet]))

(defn ctest-props []
  (com.iggroup.wt.testservice.util/app-properties
    ["default.properties.clj"
     (clojure.java.io/file "../resources/src/main/properties/ctest/properties.clj")]))

(defn ok [body]
  {:status 200
   :body   (json/generate-string body)})

(let [props (ctest-props)]
  (deftest recommend
    (fact "it recommends"
          )))