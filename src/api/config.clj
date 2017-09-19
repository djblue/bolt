(ns api.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn load-config! []
  (or
   (->> ["bolt.edn"
         "/etc/bolt/bolt.edn"]
        (map io/file)
        (filter #(. % exists))
        (map slurp)
        (map edn/read-string)
        first)
   {:bolt/port 0
    :bolt/root-dir "./comics"}))

(defn wrap-config [config]
  (fn [handler] #(-> % (assoc :config config) handler)))

(comment
  (keys (load-config!)))
