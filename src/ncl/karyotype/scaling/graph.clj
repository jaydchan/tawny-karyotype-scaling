(ns ncl.karyotype.scaling.graph
  (:use (incanter core stats charts))
  (:require [clojure.java.io :as io]))

;; function to read a file
(defn get-lines [file-name]
  (with-open [r (io/reader file-name)]
    (doall (line-seq r))))

;; reads in data
(def string-results (get-lines "results.txt"))
(def results
  (for [r string-results]
    (read-string r)))

;; groups data by the key
(def grouped-results (group-by first results))

(def stuff
  (for [r grouped-results]
    (let [record (second r)]
      [(first r)
       (apply merge
        (map
         #(sorted-map (keyword (str (second %1))) (mean (second (rest %1))))
         record))])))

(doseq [s stuff]
  (println s))

(let [start (first stuff)]
  (def plot (line-chart
             (map #(read-string (name %)) (keys (second start)))
             (map #(log %) (vals (second start)))
             ;; (vals (second start))
             :legend true
             :title "Line chart showing the average time taken (n =
             100 iterations) versus the number of random
             karyotypes (k) with at most m number of restrictions."
             :x-label "Number of random karyotypes (k)"
             :y-label "Time taken (msecs)"
             :series-label (str "m = " (first start))))
  (doseq [r (rest stuff)]
    (add-categories plot
                    (map #(read-string (name %)) (keys (second r)))
                    (map #(log %) (vals (second r)))
                    ;; (vals (second r))
                    :series-label (str "m = " (first r))))
  (view plot)
  (Thread/sleep 10000)
  (save plot "log-linear.png")
  ;; (save plot "linear-linear.png")
)