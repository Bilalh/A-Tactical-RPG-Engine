#!/bin/sh
# Spilts a tilemap
mkdir -p spilt
for (( x = 20; x < 752; x+=35 )); do
	# convert -extract "32x16+335+584" all.png "$x".png
	# convert -extract "32x16+$x+584" all.png "spilt/$x.png"
	for (( y = 8; y < 624; y+=16 )); do
		convert -extract "32x16+$(($x+30))+$y" all.png "spilt/$x-$y.png"
	done
	exit
done