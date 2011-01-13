#!/bin/bash

# if running the standalone jar produces the error "Exception in
# thread "main" java.lang.SecurityException: no manifiest section for
# signature file entry org/bouncycastle/sasn1/DerObject.class" then
# run this script. It deletes stale signature files that cause this
# error.

jarfile=pdfoffset-standalone.jar
# delete signature files from jar file:
zip -d pdfoffset.jar META-INF/BCKEY.SF META-INF/BCKEY.DSA
