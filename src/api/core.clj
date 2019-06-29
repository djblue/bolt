(ns api.core
  (:require [api.books :as books]
            [api.config :as config]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :refer [run-server]]
            [ring.logger :as logger]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [ring.middleware.gzip :as gzip]
            [ring.middleware.not-modified :as not-modified]
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as keyword-params])
  (:gen-class))

(defroutes handler
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/api/books" {:keys [config]}
    {:body (books/find-files (:bolt/root-dir config))})
  (GET "/api/books/:id/pages" [id]
    {:body (books/list-content id)})
  (GET "/api/books/:id/pages/:page-id" [id page-id width]
    {:headers {"cache-control" "max-age=31556926"
               "content-type" "image/jpeg"}
     :body (books/get-content id
                              (Integer/parseInt page-id)
                              {:width (if-not (nil? width) (Integer/parseInt width))})})
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def cfg (config/load-config!))

(def api (-> handler
             ((config/wrap-config cfg))
             (wrap-restful-format :formats [:transit-json])
             keyword-params/wrap-keyword-params
             params/wrap-params
             gzip/wrap-gzip
             not-modified/wrap-not-modified))

(defn -main []
  (run-server (-> api logger/wrap-with-logger)
              {:port (:bolt/port cfg)}))
