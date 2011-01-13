(defproject pdfoffset "1.0"
  :description "pdfoffset - A tool to change the offset between
  logical and printed page numbers in PDF files."
  :author "Bjoern Rueffer"
  :url "http://bjoern.rueffer.info"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [com.lowagie/itext "2.1.5"] ; the last LGPL release,
					     ; compatible with EPL, is
					     ; 2.1.6, which seems
					     ; unavailable
					     ; [cocoon/cocoon-itext
					     ; "2.1.6"]
		 ]
  :dev-dependencies [[swank-clojure "1.2.1"]] 
  :uberjar-name "pdfoffset.jar"
  :jar-name "pdfoffset-forlinking.jar"
  :main pdfoffset.core)
  
