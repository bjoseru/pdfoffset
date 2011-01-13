;; Copyright (C) 2011 by Bjoern Rueffer, http://bjoern.rueffer.info
;;
;; Distributed under the terms of the Eclipse Public License
;; (EPL). This software comes with NO WARRANTY.

(ns pdfoffset.core
  (:import
   (java.lang Integer)
   (java.io File FileOutputStream)
   (com.lowagie.text.pdf PdfWriter PdfReader PdfPageLabels PdfStamper)
   )
  (:gen-class)
  (:use clojure.contrib.command-line)
  )

(defn error "Output an error message." [message] (println "Error:" message))

(defn addpdfoffset
  "The core function: Use offsets for logical page numbering. Read
input file and write modified PDF to output file (overwrite if it
exists).

Need arabicfirstpage >= romanfirstpage >= 1.  The first few pages
before romanfirstpage are numbered with letters, the pages from
romanfirstpage up to arabicfirstpage-1 are numbered with roman
numerals, then from arabicfirstpage onwards they are numbered in
arabic numbers."
  
  [infile outfile arabicfirstpage romanfirstpage]
  (let [reader (PdfReader. infile)
	stamper (PdfStamper. reader (FileOutputStream. outfile))
	writer (. stamper getWriter)
	;; it is crucial not to capsulate the writer into the
	;; setPageLabels invocation: for some clouded reason this does
	;; not work
	]
    (. writer setPageLabels
       (if (>= arabicfirstpage romanfirstpage 1)
	 (if (> arabicfirstpage romanfirstpage 1)
	   (doto (PdfPageLabels.)
	     (.addPageLabel 1 PdfPageLabels/UPPERCASE_LETTERS)
	     ;; (.addPageLabel 1 PdfPageLabels/LOWERCASE_LETTERS)
	     (.addPageLabel romanfirstpage PdfPageLabels/LOWERCASE_ROMAN_NUMERALS)
	     (.addPageLabel arabicfirstpage
			    PdfPageLabels/DECIMAL_ARABIC_NUMERALS))
	   (if (> arabicfirstpage romanfirstpage) ; implicit romanfirstpage==1
	     (doto (PdfPageLabels.)
	       (.addPageLabel 1 PdfPageLabels/LOWERCASE_ROMAN_NUMERALS)
	       (.addPageLabel arabicfirstpage
			      PdfPageLabels/DECIMAL_ARABIC_NUMERALS))
	     (if (> romanfirstpage 1)	; implicit: arabicfirstpage==romanfirstpage
	       (doto (PdfPageLabels.)
		 (.addPageLabel 1 PdfPageLabels/UPPERCASE_LETTERS)
		 (.addPageLabel arabicfirstpage
				PdfPageLabels/DECIMAL_ARABIC_NUMERALS))
	       ;; else: (== arabicfirstpage romanfirstpage 1) can be assumed true
	       (doto (PdfPageLabels.)
		 (.addPageLabel 1
				PdfPageLabels/DECIMAL_ARABIC_NUMERALS))
	       )))
	 ;; fallback solution:
	 (doto (PdfPageLabels.)
	   (.addPageLabel 1 PdfPageLabels/DECIMAL_ARABIC_NUMERALS))))
    (. stamper close)))

(defn -main [& args]
  (with-command-line args
    "Description: This is pdfoffset. A tool for adjusting page number
offsets in PDF files.
Usage: pdfoffset [options] <infile> <outfile>"

    [ [arabicoffset offset o a "Physical page number of logical first
       page (numbered with arabic numbers, >= romanoffset >= 1)" 1]
      
      [romanoffset r "Physical page number of first page to be
      numbered with roman numerals (<= arabicoffset, >= 1). Everything
      before this page will be numbered with letters." 1]

      [license? "Display copyright and license information"]
      remaining ]

    (if license? (println "
Copyright (C) 2011 Bjoern S. Rueffer. http://bjoern.rueffer.info

This software (pdfoffset) comes with NO WARRANTY. It is licensed under
the Eclipse Public License, which is bundled with the source code and
also can be obtained from http://www.eclipse.org/legal/epl-v10.html.

This software (pdfoffset) relies on third-party software libraries
which underly different copyright ownership and different software
distribution libraries as follows:

* Clojure, a lisp dialect for the Java Virtual Machine, copyright
  2008-2010 Rich Hickey, distributed under the terms of the Eclipse
  Public License.

* iText 2.1.5, a Java PDF library distributed under the terms of the
  GNU Library General Public License v.2 and the Mozilla Public
  License v1.1. The copyright (1999?-2009) on iText 2.1.5 is held by a
  number of authors, mainly Bruno Lowagie, Carsten Hammer, Paulo
  Soares, Mark Thompson, Hans-Werner Hilse, Mark Hall, Howard Shank,
  Thomas Bickel, Roger Mistelli, and possibly others. iText also
  contains code underlying copyright held by The Apache Software
  Foundation, and distributed under the terms of the Apache License,
  Version 2.0, see http://www.apache.org/licenses/LICENSE-2.0.
  Furthermore, iText contains font files with the following copyright
  notice: Copyright (c) 1989, 1990, 1991, 1993, 1997 Adobe Systems
  Incorporated.  All Rights Reserved. 

* The Legion of the Bouncy Castle Java cryptography APIs:
  Copyright (c) 2000 - 2006 The Legion Of The Bouncy
  Castle (http://www.bouncycastle.org)

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the \"Software\"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
  ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.

  The source code for the cryptographic API can be obtained from
  http://bouncycastle.gva.es/www.bouncycastle.org/latest_releases.html

The source code of pdfoffset can be obtained from
http://github.com/bjoseru/pdfoffset.
")     
    
	(if (= (count remaining) 2)
	  (if (string? (first remaining))
	    (if (string? (first (rest remaining)))
	      (if (and (integer? (Integer. arabicoffset)) (integer? (Integer. romanoffset)))
		(let [infile (first remaining)
		      outfile (first (rest remaining))]
		  (println "Reading file" infile ", changing the offset to" arabicoffset "and writing to" outfile)
		  (addpdfoffset infile outfile (Integer. arabicoffset) (Integer. romanoffset)))
		(error "The offsets must be positive integers."))
	      (error "The second non-option argument must be a string (the output filename!)."))
	    (error "The first non-option argument must be a string (the input filename!)."))
	  (if (= (count remaining) 0)
	    (apply -main '("--help"))
	    (error "Expecting exactly two filenames."))
	  )))
  )

;; tests: 
;; (apply -main '("--help"))
;; (apply -main '("--license"))
;; (apply -main '("--offset" "3" "/tmp/infile.pdf" "/tmp/outfile.pdf"))
;; (apply -main '("--offset" "3" "-r" "2" "/tmp/infile.pdf" "/tmp/outfile.pdf"))

