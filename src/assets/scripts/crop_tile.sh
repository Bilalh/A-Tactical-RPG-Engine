#!/bin/sh
# Only the non-transparent are shown afther src is mapped to dst
convert dst.png src.png  -gravity center -compose atop -composite rst.png