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
definitions to include affects data property."
      :author "Jennifer Warrender"}
  ncl.karyotype.scaling.affects3
  (:use [tawny.owl])
  (:require [ncl.karyotype
             [karyotype :as k]
             [human :as h]
             [events :as e]]
            [ncl.karyotype.scaling.affects1 :as a]))

(defontology affects3
  :iri "http://www.purl.org/captau/karyotype/affects3"
  :prefix "af3:"
  :comment "Affects (take 3) ontology for Human Karyotype Ontology,
  written using the tawny-owl library."
  :noname true)

(defdproperty affects)
(defdproperty hasOrdinalNumber)

;; AUXILIARY FUNCTIONS

;; TODO Awful!!!
;; (defn generate-ordinal-number [band]
;;   (let [str-band (re-find #"[\dXY]+Band[pq][\d\.]+" (str band))
;;         chromosome (re-find #"[\d+XY]" str-band)
;;         arm (if (= "p" (re-find #"[pq]" str-band)) "0" "1")
;;         band-name (subs (re-find #"[pq][\d\.]+" str-band) 1)
;;         band-ordinal (int (* (read-string band-name) 10))]
;;     (read-string (str "1" chromosome arm band-ordinal))))

(defn- get-ordinal
  "Returns the ordinal number for given band."
  [band]
  (+ 1 (.indexOf a/bands-300 band)))

;; PATTERNS
;; (defn- affects-band
;;   "Pattern -- returns data-only axiom for BANDS using affects data
;;  property."
;;   [bands]
;;   (let [data-range (apply data-oneof
;;                     (map #(literal (get-ordinal %)) bands))]
;;     (list (data-some affects data-range)
;;           (data-only affects data-range))))

(defn- affects-band
  "Pattern -- returns data span axiom for BANDS using affects data
 property."
  [bands]
  (let [start (get-ordinal (if (vector? bands) (first bands) bands))
        finish (get-ordinal (if (vector? bands) (last bands) bands))]
    (data-some affects (min-max-inc start finish))))

(defn- get-affects
  "Returns a list of affects restrictions for a given CLAZZ in
ontology O."
  [o clazz]
  (flatten
   (for [bands (a/get-bands o clazz)]
     (affects-band bands))))

;; DRIVERS
(defn affects3-driver
  "Driver -- Returns the updated class definition of CLAZZ in ontology O."
  [o clazz]
  (let [bands (get-affects o clazz)]
    (if (= (count bands) 0)
      clazz
      (refine o
              clazz
              :subclass (affects-band bands)))))

(defn set-ordinal
  "Set ordinal value for each chromosome"
  [o]
  (doseq [clazz a/bands-300]
    (refine o
            clazz
            :subclass (data-has-value hasOrdinalNumber
                                      (literal (get-ordinal clazz))))))
