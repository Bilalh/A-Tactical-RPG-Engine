#!/bin/sh
# Set the color transparent
if [ $# -lt 1 ]; then 
	echo "$0 { color | rgb(r,g,b) | #ddddff } src [dst]"
	exit 	
fi
convert  -transparent "$1" "$2" "${3:-${2%.*}-rst.png}"
