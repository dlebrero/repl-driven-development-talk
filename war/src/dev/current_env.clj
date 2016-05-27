 (ns current-env
  (:require clojure.tools.namespace.repl))

(clojure.tools.namespace.repl/disable-reload!)
(def env (atom :dev))