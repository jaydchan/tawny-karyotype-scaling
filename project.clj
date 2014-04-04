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

(defproject ncl.karyotype.scaling "0.1.0-SNAPSHOT"
  :description "Investigating scaling performance of the Karyotype Ontology."
  :dependencies [[ncl.karyotype "1.0.0-SNAPSHOT"]]
  :scm {:url "https://github.com/jaydchan/tawny-karyotype-scaling.git"
        :name "git"}
  :license {:name "LGPL"
            :url "http://www.gnu.org/licenses/lgpl-3.0.txt"
            :distribution :repo}
  :jvm-opts ["-Xmx2g" "-server"]
  :main ncl.karyotype.scaling.core)
