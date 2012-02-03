#!/bin/sh
# Remove blank tiles 
# BLANK_HASH=${1:-7b9ee5aabc99c39755b3bf0d96652469}
BLANK_HASH=${1:-37f218c439e87ad1bc8ae45dee177f0d}
mkdir -p same
for i in *.png; do 
a=`md5 $i | grep -oP ' [a-zA-Z0-9]+'`; 
if [ "$a" == " $BLANK_HASH" ]; then 
	echo "moved $i to same/$i"
	mv "$i" "same/$i"; 
fi; 
done