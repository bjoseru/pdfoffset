# pdfoffset

A tool to change the offset between logical and printed page numbers
in PDF files.

## Binary download

A binary package can be downloaded from the [author's web site](http://bjoern.rueffer.info).

## Usage

As a java jar file:

    $ java -jar pdfoffset [--offset <page one>] <infile> <outfile>

The invocation of java can be imbedded into shell script like this
one:

    #!/bin/bash
    java -jar /path/to/pdfoffset.jar "$@"

Then the usage simply becomes:

    $ pdfoffset [--offset <page one>] <infile> <outfile>

## Compilation

This software was written in [Clojure](http://clojure.org/) using the
build tool [leinigen](http://github.com/technomancy/leiningen/). With
leiningen the compilation works as follows:

    $ lein deps
    $ lein uberjar
    $ ./repack-jar.sh

Network access is required for the first step, as the linked software
packages (clojure, iText, The Bouncy Castle Java cryptography APIs)
have to be downloaded.

The last step deletes stale signature files from the resulting jar
file, which, if present, cause the program execution to fail.

## License

Copyright (C) Bj&ouml;rn R&uuml;ffer 2011, [contact information](http://bjoern.rueffer.info)

**THIS SOFTWARE COMES WITH NO WARRANRY WHATSOEVER!**

This software is distributed under the Eclipse Public License (EPL),
the same as Clojure. You should have received a copy of the EPL along
with this software, if not, you can find the license at
[http://www.eclipse.org/legal/epl-v10.html](http://www.eclipse.org/legal/epl-v10.html).

The source code of pdfoffset can be obtained from
[http://github.com/bjoseru/pdfoffset](http://github.com/bjoseru/pdfoffset).

This software also links to third-party software libraries which are
distributed under different terms as outlined in the source file
src/pdfoffset/core.clj.
