(ns com.iggroup.wt.testservice.servlet-handler
  "Useless ns, but we have to avoid reloading the var
  that is used by the tomcat, as reloading means that
  the tomcat is actually pointing to the original var.
  All this is useful during dev time"
  (:require clojure.tools.namespace.repl))

(clojure.tools.namespace.repl/disable-reload!)
(def servlet-handler)