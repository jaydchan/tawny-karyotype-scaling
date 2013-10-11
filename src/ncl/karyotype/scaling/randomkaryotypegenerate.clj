;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2013, Newcastle University

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Lesser General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Lesser General Public License for more details.

;; You should have received a copy of the GNU Lesser General Public License
;; along with this program.  If not, see http://www.gnu.org/licenses/.

(ns ncl.karyotype.scaling.randomkaryotypegenerate
  (:use [tawny.owl])
  (:require [tawny [reasoner :as rea]]
            [ncl.karyotype [random :as ran]]
            [ncl.karyotype [karyotype :as k]]))

(defn get-value [string]
  (read-string (re-find #"[\d.]+" string)))

(defn run-once [n m k]
  (if (= 0 (mod n 10))
    (println (str "N = " n)))

  (defontology temp)

  (println "Generating")
  (time (ran/random-karyotype-driver k m))

  (rea/reasoner-factory :hermit)
  (binding [rea/*reasoner-progress-monitor*
            (atom
             rea/reasoner-progress-monitor-silent)]
  (println "Reasoning")
  (get-value (with-out-str (time
   (rea/coherent? temp)))))
)

(defn output
  "Outputs STRING to OUTPUT-FILE unless there is an ERROR"
  [output-file string append error]
   (try
     (spit output-file string
     :append append)
   (catch
       Exception exp (println error exp))))

(defn run-n-times [n-value m-values k-values]
  ;; clearing file
  (output "results.txt" "" false "Error - clearing file causes ")

  (doseq [m m-values]
    (doseq [k k-values]
      (output "results.txt"
              (str
               [m k (into [] (for [n (range n-value)]
                      (run-once n m k)))] "\n")
              true
              "Error - run-n-time causes ")))

    (println "Finished"))

;; Number of random karyotypes - it's not happy doing 1,000,000 and
;; fails for some 100,000
(def powers-of-10 (take 4 (iterate (partial * 10) 10)))
;; Max number of abnormalities
(def max-values [1 3 5 10])
;; Number of iterations
(def n 100)

(run-n-times n max-values powers-of-10)