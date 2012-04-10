#!/bin/sh
# Uses javatohtml (j2h.jar) to pretty print the source code

java -jar j2h.jar -m 3 -d srcHtml -js ../src