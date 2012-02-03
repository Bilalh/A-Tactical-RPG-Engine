#!/bin/sh
# Make the tile out the tileset
mkdir -p __rst
for i in *.png; do  
	convert mask.png "$i"  -gravity center -compose atop -composite "__rst/$i"; 
done;