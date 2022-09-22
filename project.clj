(defproject clinic "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring/ring-core "1.9.6"] ;; essential functions
                 [ring/ring-jetty-adapter "1.9.6"] ;; Jetty server and adapter
                 [compojure "1.7.0"] ;; routing library
                 ;; postgresql db connecting
                 [org.postgresql/postgresql "42.4.0"]
                 [com.github.seancorfield/next.jdbc "1.2.780"]
                 [com.h2database/h2 "1.4.199"]
                 ;; html generator
                 [hiccup "2.0.0-alpha2"]
                 ]
  :main ^:skip-aot clinic.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns clinic.core})
