#!/bin/bash
# Only the non-transparent are shown afther src is mapped to dst
convert ${2:-mask.png} ${1:-src.png}  -gravity center -compose atop -composite ${3:-${1%.*}-rst.png}