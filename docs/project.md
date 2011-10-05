Title: A Tactical RPG Engine

Introduction 
============

RPG

A Tactical RPG is a grid based game (like chess) where each player has a number of units(pieces). The players taken turns to moves their units.  Each unit has attributes associated with it such as strength, and hit points that affect all the actions in the game. Like chess there are different kinds of units which  affects how the unit moves and what action they can perform. A unit can attack other player's units, the goal of the game is usually to defeat all the opponents units.   

The aim of this project is to create a engine which will take resources such as graphics, sound and rules of the game to create a runnable Tactical RPG.

Objectives
==========

Primary 
-------
* To develop an engine that takes: 
	* The  definition  of  character  attributes  and  a  combat  system. 
	* The  definition  of  a  world  broken  up  into  the  smaller  environments.  
	* The  definition  of  simple  story  as  a  wrapper  for  the  whole  game, from  the  start  to  the
	  conclusion  of  the  game

and create a playable tactic RPG. 

* To include in the engine support for the following:
	* `units` with associated attributes such as:
		* Hit-points (which represent the health of the unit).
		* Strength.
		* Defence.
		* Move (The number of tiles the unit can move each turn).
	*  `battles` which take place on grid and include:
		*  A set number of `units` for each player.
		* `combat` between adjacent units.
			* When the unit hit-points are reduced to zero they are `defeated` and are removed from the map.  
		* A Winning `condition ` such as defeat all of the other players units.
		* Battles are `turn based` meaning that each player moves all their units (once) 
		  before the next player turn.
	* Movement between different environments.  
	* A set  of  behaviours  for  how  the  non-player characters  should  behave.
		* Including pathfinding.
	* A graphical representation of of the game. 
	
Secondary
---------
* Tile `height`, where units can only move to tiles of a smilier height.
* Tiles that are not passable such as sea, lava, etc.
* Tiles have different movement costs associated with them. 
* Isometric graphics view of the game.
* Long distance weapons/magic for player and AI.
* Direction and height of the character's tile affects attack.
* Sound effects.
* Music.
* Saving and loading games.
* A default set of behaviours for non-player characters. 

Tertiary
--------
* Scripting support e.g to allow the user to defined winning conditions.
* A graphical editor for making custom maps and events.
* Animations for units, combat and movement.
* Creation of custom units and shops.
 

Ethical Considerations 
----------------------
* form by 28th October.
* Collection of data from questionnaire. 
	*  Just result of questionnaire, no personal data.
* Asking users to create a game.
* Asking users to play the created game.

Resources
---------
* None.
