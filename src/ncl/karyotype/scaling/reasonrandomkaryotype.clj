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

(ns ncl.karyotype.scaling.reasonrandomkaryotype
  (:use [tawny.owl])
  (:require [tawny [reasoner :as rea]]
            [ncl.karyotype [random :as ran]]
            [ncl.karyotype [karyotype :as k]]))

(defn get-value [string]
  (read-string (re-find #"[\d.]+" string)))

(defn run-once [n m k]
  (def temp (.loadOntologyFromOntologyDocument
             (owl-ontology-manager)
             (tawny.owl/iri (clojure.java.io/resource (str "n" n ".owl")))))

  (rea/reasoner-factory :hermit)
  (binding [rea/*reasoner-progress-monitor*
            (atom
             rea/reasoner-progress-monitor-silent)]
  (get-value
   (with-out-str (time
                  (try
                    (rea/coherent? temp)
                    (catch Exception e nil)))))))

;; MAIN
;; Max number of abnormalities
(def m (read-string (slurp "m.txt")))
;; Number of random karyotypes
(def k (read-string (slurp "k.txt")))
;; Number of iteration
(def n (read-string (slurp "n.txt")))
(println (run-once n m k))