#!/bin/sh
mkdir -p ' rst'
for i in *.jpg *.png ; do  make_iso_tile.sh "$i"; done
for i in *rst.png ; do  set_alpha.sh white "$i" " rst/$i" ;  done