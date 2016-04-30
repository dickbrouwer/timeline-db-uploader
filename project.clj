(defproject db-uploader "0.1.0"
  :description "Concurrent S3 file uploader for Timeline"
  :url "N/A"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.1.19"]
                 [org.clojure/tools.cli "0.3.3"]
                 [throttler "1.0.0"]
                 ]
  :main db-uploader.core
  :aot :all
  )
