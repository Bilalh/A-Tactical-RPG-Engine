#!/bin/sh
# Spilts a tilemap
for (( x = 0; x < 752; x+=35 )); do
	convert -extract "32x16+335+584" all.png "$x".png
	exit
done