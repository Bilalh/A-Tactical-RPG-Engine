#!/bin/bash
if [ $# -lt 1 ]; then 
	echo "$0 src [rst.png]"
	exit 	
fi
convert -trim -rotate 45  "$1" "__temp-$1.png"
convert -trim -resize x50% "__temp-$1.png" "${2:-${1%.*}-rst.png}"