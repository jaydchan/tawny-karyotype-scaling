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

(ns ncl.karyotype.scaling.core
  (:use [tawny.owl])
  (:require [tawny.reasoner]
            [ncl.karyotype random]
;;            [ncl.karyotype.scaling randomkaryotype1010]
;;            [ncl.karyotype.scaling randomkaryotypegenerate]
            [ncl.karyotype.scaling graph]
            )
  (:gen-class))

(defn -main [& args]
  ;; (with-ontology ncl.karyotype.scaling.randomkaryotype1010/randomkaryotype1010
  ;;   (save-ontology "randomkaryotype1010.omn" :omn)
  ;;   (save-ontology "randomkaryotype1010.owl" :owl))
)