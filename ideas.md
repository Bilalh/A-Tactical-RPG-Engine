FIXME 
-----
Check that finalHeight was always the same.

AI
---
* Pathfinding -- done 
  * targets the unit with lowest hp for now.


Editor
-----
* Added image to tilemap on save. -- done
* Main editor for making game
* Ai Unit placement
* Unit creation
* Item Creation -- done
* Dialog loading

ideas
-----
* Use ref counting for resources in editor, this allows checking e.g if a weapon is used, before it deleted 
* Use base64 encoding.

View
---
* Unit crossing in gui -- done
* Going to next map    -- done 
* Rare bug when click unit
* Zoom  -- done
* Pitch -- done
* Resize images -- done
* Rotate   -- done 
  * Units  -- done
* refactor -- done
* Skip empty tiles -- done
* Wait util music is finished
* Slanted tiles -- kind of 

GUI
---
* World map
* Dialog bugs -- fixed

Save/Loading
------------
* Skills  -- done
* Weapons -- done
* Units   -- kind of
* uuid    -- done

Model 
-----
* Skills -- done
* Healing skill 
* spears -- done
* bows   -- done

Misc
----
* Music in its own thread -- done
* Fixed music not playing after pausing -- done
* Rotate map -- done 
	* Rotate units  -- done
	* Rotate Cursor -- done
* Zoom  -- done
* Pitch -- done
* Selection above range -- done
* Map scrolling to current units turn --  maybe?
* Right click as cancel -- done
* Console as history of the game -- done

State
-----
* Make fsm -- done
* Wait     -- done
* Menus    -- done 
	* Mouse and keyboard input -- done


Map Drawing
-----------

*cache polygon -- not worth it
* Cache map good but  
	* unit drawing in reguard to height wrong 
	* redraw on each movement -- ok
		
		* Draw all of A's  and only draw the tops of T's
	 
		*    
		-
	   -*-
	  -A-A-
	 -T-T-
      -T-T-
       - -
       * Works but nearly as bad as redrawing.
       
       * Partial redrawing -- does not work, wrong draw order at ends  

* Better way -- 
	* Cache map 
	* overall selected tile/movement range with bitwise drawing ops 
	* for units find bounding box then use bitwise ops to redraw the area around unit

* Choosen Way
	* Cache for a while then redraw

