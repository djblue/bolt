(defproject bolt "1.0.0"
  :description "A graphic novel reader focused on speed."
  :url "https://github.com/djblue/reader"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443" :exclusions [org.clojure/tools.reader]]
                 [http-kit "2.2.0"]
                 [ring-logger "0.7.7"]
                 [bk/ring-gzip "0.2.1"]
                 [me.raynes/fs "1.4.6"]
                 [image-resizer "0.1.10"]
                 [compojure "1.6.0"]
                 [ring-middleware-format "0.7.2" :exclusions [ring/ring-core
                                                              commons-codec
                                                              commons-fileupload]]
                 [com.github.beothorn/junrar "0.6"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-project-version "0.1.0"]]
  :profiles {:cljs {:dependencies
                    [[org.clojure/clojurescript "1.9.521"]
                     [cljs-http "0.1.43"]
                     [reagent "0.7.0" :exclusions [cljsjs/react]]
                     [cljsjs/react-with-addons "15.5.4-0"]
                     [garden "1.3.2"]
                     [cljs-css-modules "0.2.1"]
                     [alandipert/storage-atom "2.0.1"]]}
             :prod {:cljsbuild {:builds
                                {:app
                                 {:compiler
                                  {:optimizations :advanced
                                   :pretty-print false}}}}}
             :dev {:cljsbuild {:builds
                               {:app
                                {:figwheel {:websocket-host :js-client-host
                                            :on-jsload "app.core/render"}}}}
                   :dependencies
                   [[cljfmt "0.5.7" :exclusions [org.clojure/tools.reader
                                                 org.clojure/clojurescript]]
                    [figwheel-sidecar "0.5.12" :exclusions [org.clojure/tools.nrepl
                                                            ring/ring-core
                                                            org.clojure/tools.reader
                                                            org.clojure/clojurescript]]
                    [com.cemerick/piggieback "0.2.2"]]
                   :repl-options
                   {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                    :init (do
                            (use 'figwheel-sidecar.repl-api)
                            (start-figwheel!)
                            (. System/out (println "Piggieback (figwheel-sidecar.repl-api/repl-env)")))}}
             :uberjar {:auto-clean false
                       :aot :all
                       :omit-source true}}
  :main api.core
  :resource-paths ["resources" "target/resources/"]
  :uberjar-exclusions [#"\.(clj|cljs|java|xml|md|txt)"]
  :cljsbuild {:builds
              {:app
               {:source-paths ["src"]
                :compiler {:main "app.core"
                           :asset-path "out"
                           :output-to "target/resources/public/main.js"}}}}
  :figwheel {:ring-handler api.core/api}
  :aliases {"start" [["with-profile" "+dev,+cljs" "repl"]]
            "package" ["do"
                       "clean"
                       ["with-profile" "+prod,+cljs" "cljsbuild" "once"]
                       ["uberjar"]]})
