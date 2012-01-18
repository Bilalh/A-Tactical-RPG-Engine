#!/bin/sh
# Remove blank tiles 
BLANK_HASH="7b9ee5aabc99c39755b3bf0d96652469"
mkdir -p same
for i in *-*.png; do 
a=`md5 $i | grep -oP ' [a-zA-Z0-9]+'`; 
if [ "$a" == " $BLANK_HASH" ]; then 
	echo "moved $i to same/$i"
	mv "$i" "same/$i"; 
fi; 
done