AI
--
* Pathfinding -- done 
  * targets the unit with lowest hp for now.


Editor
-----
* Added image to tilemap on save. -- done


GUI
---
* Unit crossing in gui -- done
* Going to next map    -- done 
* Rare bug when click unit
* Zoom  -- done
* Pitch -- done
* Resize images
* Rotate -- done 
  * Units -- done
* refactor -- done
* Disallow pitch changing when zoom is one

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

