(ns ncl.karyotype.scaling.graph
  (:use (incanter core stats charts))
  (:require [clojure.java.io :as io]))

(defn- get-lines [file-name]
  "Reads in FILE_NAME."
  (with-open [r (io/reader file-name)]
    (doall (line-seq r))))

(defn- performance-plot [data]
  "Produces (and saves) scatter plot showing performance results using
given DATA."
  (let [times (for [r data]
                (get r 4))]
    (def performance (scatter-plot
                      (range 1 (count times))
                      times
                      :title "Performance check"
                      :x-label "Run index"
                      :y-label "Time taken (ms)")))
  ;; (view performance)
  (save performance "performance.png"))

(defn- scaling-plot [data]
  "Produces (and saves) line chart showing scaling results using given
DATA."
  (let [start (first data)]
    (def scaling (line-chart
               (map #(read-string (name %)) (keys (second start)))
               (map #(log %) (vals (second start)))
               ;; (vals (second start))
               :legend true
               :title "Scaling results"
               :x-label "Number of random karyotypes (k)"
               :y-label "Time taken (ms)"
               :series-label (str "m = " (first start))))
    (doseq [r (rest data)]
      (add-categories scaling
                      (map #(read-string (name %)) (keys (second r)))
                      (map #(log %) (vals (second r)))
                      ;; (vals (second start))
                      :series-label (str "m = " (first r))))
    ;; (view scaling)
    ;; (Thread/sleep 1000) ;; needed???
    (save scaling "scaling.png")))

(import '(org.jfree.chart.renderer.category BarRenderer)
        '(java.awt Color))

(def colors [Color/blue Color/red Color/green Color/yellow])
(defn- affects-plot [data]
  "Produces (and saves) bar charts showing affects results using given
DATA."
  (doseq [d data]
    (let [chart (bar-chart
                (map #(read-string (name %)) (keys (second d)))
                (vals (second d))
                :title (str (first d))
                :x-label "Affects model (a)"
                :y-label "Time taken (ms)")]
      ;; custom-render class (and instance) such that bars are
      ;; different colors
      ;; http://www.java2s.com/Code/Java/Chart/JFreeChartBarChartDemo3differentcolorswithinaseries.htm
      ;; http://stackoverflow.com/questions/1112709/when-you-extend-a-java-class-in-clojure-and-define-a-method-of-the-same-name-as
      (def custom-render
        (proxy [BarRenderer] []
          (getItemPaint [row column]
            (get colors (mod column (count colors))))))
      (.setRenderer (.getPlot chart) custom-render)
      ;; (view chart)
      (save chart (str "affects" (first d) ".png")))))

(defn driver [avalue mvalue]
  ;; reads in data
  (def string-results (get-lines "results.txt"))
  (def results
    (for [r string-results]
      (read-string r)))

  ;; PERFORMANCE CHECK
  (performance-plot results)

  ;; SCALING RESULTS
  (let [a-groups (group-by first results)
        affects (get a-groups avalue)
        m-groups (group-by #(get % 2) affects)]

    (def scaling
      (for [m m-groups]
        (let [records (second m)
              k-groups (group-by #(get % 1) records)]
          [(first m)
           (apply merge
                  (map
                   #(sorted-map (keyword (str (first %)))
                                (mean (for [record (second %)]
                                        (get record 4))))
                   k-groups))]))))
  (scaling-plot scaling)

  ;; AFFECTS RESULTS
  (let [m-groups (group-by #(get % 2) results)
        max (get m-groups mvalue)
        k-groups (group-by #(get % 1) max)]

    (def affects
      (for [k k-groups]
        (let [records (second k)
              a-groups (group-by #(get % 0) records)]
          [(first k)
           (apply merge
                  (map
                   #(sorted-map (keyword (str (first %)))
                                (mean (for [record (second %)]
                                        (get record 4))))
                   a-groups))]))))
  (affects-plot affects))