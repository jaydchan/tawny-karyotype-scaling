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

(ns ncl.karyotype.scaling.stats
  (:use [tawny.owl])
  (:require
   [ncl.karyotype
    [random :as r :only [RandomKaryotype]]
    [parse :as p :only [human-filter]]
    [events :as e :only [addition deletion]]
    [human :as h :only [chromosome? band?]]]
   ))

(defn- event-type
  "TODO"
  [event]
  (let [axiom (.getFiller event)]
    (cond
     ;; "If axiom is an addition event"
     (.containsConjunct axiom e/Addition)
     "addition"
     ;; "If axiom is a deletion event"
     (.containsConjunct axiom e/Deletion)
     "deletion"
     :default
     (throw
      (IllegalArgumentException.
       (str "event-type expects an event restriction :only Addition or
            Deletion. Got:" event))))))

(defn- human-type
  "TODO"
  [event]
  (let [chrom_band (p/human-filter (.getFiller event))]
    (cond
     ;; "If axiom is a chromosomal event"
     (h/chromosome? (first chrom_band))
     "chromosome"
     ;; "If axiom is a chromosomal band event"
     (h/band? (first chrom_band))
     "band"
     :default
     (throw
      (IllegalArgumentException.
       (str "human-type expects an event restriction :only Chromosome
            or Band. Got:" event))))))

(defn- stats [map]
  [(get map "additionband" 0)
   (get map "additionchromosome" 0)
   (get map "deletionband" 0)
   (get map "deletionchromosome" 0)])

(defonce output-file-path "./output/")
(defn output
  "Outputs STRING to OUTPUT-FILE unless there is an ERROR"
  [output-file0 string append error0]
  (let [output-file (str output-file-path output-file0)
        error (str "Error: output-file: " output-file error0)]
   (try
     (spit output-file
           string
           :append append)
     (catch
         Exception exp (println error exp)))))

(defn run-once [n m k]

  (let [temp (tawny.read/read
              :location
              (iri (clojure.java.io/as-file
                    (str "./output/n" n ".owl")))
              :iri "http://ncl.ac.uk/karytype/tmp"
              :prefix "tmp:")]

    (output "stats.txt"
            (str "["
                 (clojure.string/join " "
                       (flatten
                        (conj [k m n]
                              (stats
                               (frequencies
                                (flatten
                                 (for [clazz (subclasses temp r/RandomKaryotype)]
                                   (for [event
                                         (filter
                                          #(instance?
                                            org.semanticweb.owlapi.model.OWLObjectExactCardinality %)
                                          (superclasses temp clazz))]
                                     (str (event-type event) (human-type event)))))))))) "]\n")
            true "")))