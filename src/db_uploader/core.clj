(ns db-uploader.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.cli :refer [parse-opts]]
            [throttler.core :refer [throttle-fn]]
            )
  (:gen-class))

(defn make-put [file]
  (http/request {:url (str "https://s3.amazonaws.com/dbrouwer-test/" (.getName file))
                 :method :put
                 :body (slurp file)
                 })
  )

(def make-put-throttled
  (throttle-fn make-put 1000 :second))

(defn do-requests [files]
  (let [futures (doall (map make-put files))]
    (doseq [resp futures]
      (println (-> @resp :opts :url) " status: " (:status @resp))
      )
    )
  )

(defn run [file-count file-dir]
  (do-requests
    (take file-count
          (filter #(.isFile %)
                  (file-seq (clojure.java.io/file file-dir)))))
  )

(def cli-options
  ;; An option with a required argument
  [["-c" "--count FILE COUNT" "Number of files to process"
    :default 10
    :parse-fn #(Integer/parseInt %)]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        file-count (:count (:options opts))
        file-dir (first (:arguments opts))
        ]
    (cond
      (not= (count (:arguments opts)) 1) (exit 1 "path argument required"))
    (run file-count file-dir))
  )
