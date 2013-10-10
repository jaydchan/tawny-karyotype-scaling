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
            [ncl.karyotype [randomkaryotype :as ran]]
            [ncl.karyotype [karyotype :as k]]))

;; Number of random karyotypes - not happy doing 1,000,000
(def powers-of-10 (take 5(iterate (partial * 10) 10)))
;; Max number of abnormalities
(def max-values [1 3 5 10])

(doseq [n powers-of-10]
  (doseq [m max-values]

    (println "Generating")
    (println (str "M = " m))
    (println (str "N = " n))

    (defontology temp)

    (time (ran/random-karyotype-driver n m))

    (rea/reasoner-factory :hermit)
    (println "Reasoning")
    (time
     (println "consistent:"
              (rea/coherent? temp)))
    ))

(println "Finished")
