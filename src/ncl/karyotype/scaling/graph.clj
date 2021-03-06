(ns ncl.karyotype.scaling.graph
  (:use (incanter core stats charts)
        [ncl.karyotype.generic])
  (:require [clojure.java.io :as io]))

(defn- performance-plot
  "Produces (and saves) scatter plot showing performance results using
given DATA."
  [data]
  (let [times (for [r data]
                (get r 4))
        performance (scatter-plot
                     (range 1 (count times))
                     times
                     :title "Performance check"
                     :x-label "Run index"
                     :y-label "Time taken (ms)")]
    ;; (view performance)
    (save performance "performance.png")))

(defn- scaling-plot
  "Produces (and saves) line chart showing scaling results using given
DATA."
  [data]
  (let [start (first data)
        scaling (line-chart
                 (map #(read-string (name %)) (keys (second start)))
                 (map #(log %) (vals (second start)))
                 ;; (vals (second start))
                 :legend true
                 :title "Scaling results -- semi-log plot"
                 :x-label "Number of random karyotypes"
                 :y-label "Time taken (ms)"
                 :series-label (str "m = " (first start)))]
    (doseq [r (rest data)]
      (add-categories scaling
                      (map #(read-string (name %)) (keys (second r)))
                      (map #(log %) (vals (second r)))
                      ;; (vals (second r))
                      :series-label (str "m = " (first r))))
    ;; (view scaling)
    (save scaling "scaling.png")))

(import '(org.jfree.chart.renderer.category BarRenderer)
        '(java.awt Color))

;; custom-render class (and instance) such that bars are different colors
;; http://www.java2s.com/Code/Java/Chart/JFreeChartBarChartDemo3differentcolorswithinaseries.htm
;; http://stackoverflow.com/questions/1112709/when-you-extend-a-java-class-in-clojure-and-define-a-method-of-the-same-name-as
(def colors [Color/blue Color/red Color/green Color/yellow])
(defn- affects-plot
  "Produces (and saves) bar charts showing affects results using given
DATA."
  [data]
  (doseq [d data]
    (let [chart (bar-chart
                (map #(read-string (name %)) (keys (second d)))
                (vals (second d))
                :title (str (first d))
                :x-label "Affects model"
                :y-label "Time taken (ms)")
          custom-render
          (proxy [BarRenderer] []
            (getItemPaint [row column]
              (get colors (mod column (count colors)))))]
      (.setRenderer (.getPlot chart) custom-render)
      ;; (view chart)
      (save chart (str "affects" (first d) ".png")))))

(defn- stats-plot
  "TODO Produces (and saves) bar charts showing affects results using given
DATA."
  [data]
  (doseq [d data]
    (let [records (second d)
          labels ["+ band" "+ chrom" "- band" "- chrom"]
          stats (box-plot
                 (for [r records] (get r 0))
                 :title (str (first d))
                 :x-label "Event restriction"
                 :y-label "Number of restrictions"
                 :series-label (get labels 0)
                 :legend true)]
      (doseq [i (range 1 4)]
        (add-box-plot stats
                      (for [r records] (get r 0))
                      :series-label (get labels i)))
      ;; (view stats)
      (save stats (str "stats" (first d) ".png")))))

(defn- diff-plot
  "Produces (and saves) line chart showing diff results using given
DATA."
  [data]
  (let [start (first data)
        diff (line-chart
              (map #(read-string (name %)) (keys (second start)))
              (map #(log %) (vals (second start)))
              ;; (vals (second start))
              :legend true
              :title "Diff results -- semi-log plot"
              :x-label "Affects model"
              :y-label "Number of changes"
              :series-label (str "k = " (first start)))]
    (doseq [r (rest data)]
      (add-categories diff
                      (map #(read-string (name %)) (keys (second r)))
                      (map #(log %) (vals (second r)))
                      ;; (vals (second r))
                      :series-label (str "k = " (first r))))
    ;; (view diff)
    (save diff "diff.png")))

(defn driver
  "TODO"
  [avalue mvalue]

  ;; reads in data -- results.txt
  (let [results (read-file "./output/results.txt")]

    ;; PERFORMANCE CHECK
    (performance-plot results)

    ;; SCALING RESULTS
    (let [a-groups (group-by first results)
          affects (get a-groups avalue)
          m-groups (group-by #(get % 2) affects)]

      (scaling-plot
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

    ;; AFFECTS RESULTS
    (let [m-groups (group-by #(get % 2) results)
          max (get m-groups mvalue)
          k-groups (group-by #(get % 1) max)]

      (affects-plot
       (for [k k-groups]
         (let [records (second k)
               a-groups (group-by #(get % 0) records)]
           [(first k)
            (apply merge
                   (map
                    #(sorted-map (keyword (str (first %)))
                                 (mean (for [record (second %)]
                                         (get record 4))))
                    a-groups))])))))

    ;; STATS RESULTS
    (let [results (read-file "./output/stats.txt")
          m-groups (group-by #(get % 1) results)
          max (get m-groups mvalue)
          k-groups (group-by #(get % 0) max)]

      (stats-plot
       (for [k k-groups]
         [(first k) (for [record (second k)]
                      (subvec record 3))])))

  ;; DIFF RESULTS
  (let [results (read-file "./output/diff.txt")
        m-groups (group-by #(get % 2) results)
        max (get m-groups mvalue)
        k-groups (group-by #(get % 1) max)]

    (diff-plot
     (for [k k-groups]
       (let [records (second k)
             a-groups (group-by #(get % 0) records)]
         [(first k)
          (apply merge
                 (map
                  #(sorted-map (keyword (str (first %)))
                               (mean (for [record (second %)]
                                       (get record 4))))
                  a-groups))])))))