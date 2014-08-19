;; The contents of this file are subject to the LGPL License, Version 3.0.

;; Copyright (C) 2013-2014, Newcastle University

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

(ns ncl.karyotype.scaling.generaterandomkaryotype
  (:use [tawny.owl])
  (:require [ncl.karyotype [random :as r]]))

(defn run-once [n m k]
  (let [temp (ontology
              :name "temp"
              :prefix "tmp:"
              :iri "http://ncl.ac.uk/karyotype/tmp")]

    ;; sets current-ontology to temp ontology
    (ontology-to-namespace temp)

    (r/random-karyotype-driver temp k m)

    (save-ontology temp (str "n" n ".owl") :owl)
    (save-ontology temp (str "n" n ".omn") :omn)))