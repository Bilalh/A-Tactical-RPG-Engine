Readme
======

Binaries
--------
`Binaries` contains a package version of the engine: There are two versions

* `Tactical Engine.app` is for Mac OS X only and has Mac specify features such as a Dock Icon.
* `Tactical Engine.jar` is the cross-platform version that works on any Java enable platform

src
---
`src` contains the source code of the project.

build.xml
---------
The script can be used for compiling and creating binaries, see the comment at the top `build.xml` for more information.

	ant       # Complies all the source code 
	ant dist  # Creates self contained binaries and places then in `Binaries`

schemas
--------
Contain xml schemas for validate the xml files.

bundle
------
Contains the resources used when creating the self contained binaries. 

libs
----
Contains all the Java libraries  and native libraries.

scripts
-------
Helper scripts to create isometric tiles.

icons
-----
This directory contains the icons created for the editor. There two icons, one for the applications and for project file (`.tacticalproject`)

bin
---
Compiled Java files.

IsomerticRendering.gcx
----------------------
A simulation of the equations used to draw the tiles (Mac OS X).
Since Java's graphics (0,0) is at the top left  set y axis (by clicking on it) to 400 ... 200 to see tile correctly