(defproject triangle-gui "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [garden "1.3.6"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-ancient "0.6.15"]
            ]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :garden {:source-path "src/garden"
           :output-path "resources/css"}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"] [day8.re-frame/re-frame-10x "0.3.7-react16"]]

    :plugins      [[lein-figwheel "0.5.16"]]}
   :prod { }
   }

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "triangle-gui.core/mount-root"}
     :compiler     {:main                 triangle-gui.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                    :preloads             [day8.re-frame-10x.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            triangle-gui.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :none
                    :closure-defines {goog.DEBUG true}
                    :pretty-print    false}}


    ]}
  )
