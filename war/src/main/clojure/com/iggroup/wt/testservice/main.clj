(ns com.iggroup.wt.testservice.main
  "Namespace to bootstrap the application. Equivalent to the spring.xml"
  (:use [ring.util.response]
        [clojure.tools.logging :only [info warn error]])
  (:require [com.iggroup.wt.testservice
             [routes :as routes]
             [servlet-handler :as servlet]]
            incanter.core
            incanter.charts
            [com.iggroup.wt.testservice.util :as util]
            [clojure.tools.nrepl.server :as nrepl]))

;; This should be the only piece of global state in the app
(defonce system (atom nil))

(defn system-start
  "Given a config, it knows how to create and start a new system"
  [app-config]
  (info "Application starting up now ...")
  (let [routes (-> (var routes/http-api)
                   util/remove-context
                   (util/wrap-with-additional-keys-in-req :db "jdbc:postgresql://localhost:5432/dlebrero"))
        system {:app-config app-config
                :routes     routes}]
    (info "Application started")
    system))

(defn system-stop
  "Given a system created by system-start, it knows how to stop it"
  [system]
  (info "Application stopping ...")
  (info "Application stopped"))

(defn replace-http-api! [system]
  (alter-var-root #'servlet/servlet-handler (constantly (:routes system))))

(defn servlet-context-init
  "Fn that will be called by the servlet container that will init the app"
  [ctx]
  (let [props (util/ig-standard)]
    (when-let [nrepl-port (:nrepl.port props)]
      (info "Starting nrepl in port" nrepl-port)
      (nrepl/start-server :port nrepl-port :bind "127.0.0.1"))
    (let [new-system (reset! system (system-start props))]
      (replace-http-api! new-system))))

(defn servlet-context-destroy
  "Fn that will be called by the servlet container that will stop the app"
  [ctx]
  (system-stop @system)
  (reset! system nil))
