#!/bin/bash
# Creates isometric tiles from square images (either jpeg or png). 
# Place the sources image in  the `images' folder.
# The results will be placed in the `results` folder
# requires imagemagick  (http://www.imagemagick.org/) 
pushd images
for i in *; do
	convert -trim -rotate 45 -trim -resize x50% -transparent white "$i" "../results/${i%.*}.png"
done
popd