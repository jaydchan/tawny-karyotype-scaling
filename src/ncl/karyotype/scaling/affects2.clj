;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2013-2014, Newcastle University

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program. If not, see http://www.gnu.org/licenses/.

(ns ^{:doc "Redefining chromosomal band addition and deletion event
definitions to include affects object property and sequence pattern"
      :author "Jennifer Warrender"}
  ncl.karyotype.scaling.affects2
  (:use [tawny.owl]
        [tawny.render])
  (:require [ncl.karyotype
             [karyotype :as k]
             [human :as h]
             [events :as e]]
            [ncl.karyotype.scaling.affects1 :as a]))

(defontology affects2
  :iri "http://www.purl.org/captau/karyotype/affects2"
  :prefix "af2:"
  :comment "Affects (take 2) ontology for Human Karyotype Ontology,
  written using the tawny-owl library."
  :noname true)

(defoproperty affects)

;; TODO should really import sequence.owl.rdf
(defoproperty precedes
  :characteristic :transitive)
(defoproperty directlyPrecedes
  :super precedes
  :characteristic :functional)
(defoproperty follows)
(defoproperty directlyFollows
  :super follows
  :inverse directlyPrecedes)

;; PATTERNS
(defn- affects0 [bands]
  "Pattern -- encoded sequence ODP."
  (if (= 1 (count bands))
    (first bands)
    (owl-and
     (first bands)
     (owl-some directlyPrecedes
               (affects0 (rest bands))))))

(defn- affects-band
  "Pattern -- returns sequence ODP variant for given BANDS, using
affects object property"
  [bands]
  (list
   (owl-some affects bands)
   (if (vector? bands)
     (owl-some affects (affects0 bands)))))

;; AUXILIARY FUNCTIONS
(defn- get-affects
  "Returns a list of affects restrictions for a given CLAZZ in
ontology O."
  [o clazz]
  (flatten
   (for [bands (a/get-bands o clazz)]
     (affects-band bands))))

;; DRIVERS
(defn affects2-driver
  "Driver -- Returns the updated class definition of CLAZZ in ontology O."
  [o clazz]
  (let [bands (remove nil? (get-affects o clazz))]
    (if (= (count bands) 0)
      clazz
      (refine o
              clazz
              :subclass bands))))