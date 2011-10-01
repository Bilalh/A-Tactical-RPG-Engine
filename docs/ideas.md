Ideas
=====

Scripting 
---------

* Attached events to units, titles etc.
* Events get passed a `mapinfo` object containing.
	* a hash of the players unit and a hash of the enemies units..
	* The leader of each side if there is one.
	* The number of turns taken.
* Contains methods some as `Win` and `Lose` to allow custom victory conditions. 
* `dialog` method to make unit talk. 
	*  Either on unit or not.
* Unit events get passed the specified unit. 
	1. takes place after the units finishes it turn is affected by magic, attacked.
	2. Could be used to win if some unit has less then 50% hp.
* Tiles get passes the specified tile.
	* Event get called after a unit moves onto the tile.
	* Could be used for `treasure` or tile effects such as hp damage when on `swap` or `lava`. 

* `javax.script` supports scripting using `javascript`, `ruby` (`jruby`)  and `python` (jython).
* mozilla `rhino` also provides javascript embedding  



Units
-----

* FSM  for each state selected, moved, defeated, etc
* Either each player moves **all** his units in turn or  turn ordering is by some calculation e.g speed.
* Unit can `level up`
