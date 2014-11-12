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
;; along with this program. If not, see http://www.gnu.org/licenses/.

(ns ncl.karyotype.scaling.core
  (:use [tawny.owl])
  (:require [ncl.karyotype.random]
            [tawny.reasoner]
            [ncl.karyotype.scaling
             affects1 affects2 affects3
             generaterandomkaryotype
             [refinerandomkaryotype :as r]
             reasonrandomkaryotype
             graph stats
             ])
  (:gen-class))

(defn -main [& args]

  (let [arg0 (into [] args)]
    ;; Driver
    (def d (read-string (get arg0 0)))
    (try
      ;; Number of random karyotypes
      (def k (read-string (get arg0 1)))
      ;; Max number of abnormalities
      (def m (read-string (get arg0 2)))
      ;; Number of iteration
      (def n (read-string (get arg0 3)))
      (catch Exception e (str "Error: importing args " (count arg0)))))

  (cond
   (= d -1)
   (print
    (ncl.karyotype.scaling.reasonrandomkaryotype/run-once n m k))
   (= d 0)
   (ncl.karyotype.scaling.generaterandomkaryotype/run-once n m k)
   (= d 1)
   (r/run-once ncl.karyotype.scaling.affects1/affects1-driver k m n)
   (= d 2)
   (r/run-once ncl.karyotype.scaling.affects2/affects2-driver k m n)
   (= d 3)
   (r/run-once ncl.karyotype.scaling.affects3/affects3-driver k m n)
   (= d -2)
   (ncl.karyotype.scaling.graph/driver 0 5)
   (= d -3)
   (ncl.karyotype.scaling.stats/run-once n m k)
   :else
   (println
    (str "ERROR Expected: d in {-3..3} "
         "Actual: d = " d " with type " (type d)))))