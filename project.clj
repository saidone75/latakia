(defproject latakia "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://www.3x1t.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.844" :scope "provided"]
                 [com.cognitect/transit-clj "1.0.324"]
                 [com.cognitect/transit-cljs "0.8.264"]
                 [ring "1.9.2"]
                 [ring/ring-defaults "0.3.2"]
                 [bk/ring-gzip "0.3.0"]
                 [ring-logger "1.0.1"]
                 [clj-logging-config "1.9.12"]
                 [environ "1.2.0"]
                 [com.stuartsierra/component "1.0.0"]
                 [org.danielsz/system "0.4.7"]
                 [org.clojure/tools.namespace "1.1.0"]
                 [clj-time "0.15.2"]
                 [compojure "1.6.2"]
                 [reagent "1.0.0"]
                 [cljs-ajax "0.8.3"]
                 [likid_geimfari/secrets "0.2.0"]
                 [me.raynes/conch "0.8.0"]
                 [com.draines/postal "2.0.4"]
                 [com.taoensso/timbre "5.1.2"]
                 [levand/immuconf "0.1.0"]
                 [com.taoensso/tempura "1.2.1"]
                 [buddy/buddy-core "1.9.0"]]

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-environ "1.2.0"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js" "dev-target"]

  :uberjar-name "latakia.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main latakia.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (go) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc" "dev"]

                :figwheel {:on-jsload "latakia.system/reset"}

                :compiler {:main cljs.user
                           :asset-path "js/compiled/out"
                           :output-to "dev-target/public/js/compiled/latakia.js"
                           :output-dir "dev-target/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "dev-target/public/js/compiled/testable.js"
                           :main latakia.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main latakia.system
                           :output-to "resources/public/js/compiled/latakia.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :closure-defines {goog.DEBUG false}
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.20"]
                             [figwheel-sidecar "0.5.20"]
                             [cider/piggieback "0.5.2"]
                             [cider/cider-nrepl "0.25.11"]
                             [lein-doo "0.1.11"]
                             [reloaded.repl "0.2.4"]]

              :plugins [[lein-figwheel "0.5.18"]
                        [lein-doo "0.1.11"]]

              :source-paths ["dev"]
              :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]]
              :hooks []
              :omit-source true
              :aot :all}})
