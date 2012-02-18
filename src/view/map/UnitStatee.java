package view.map;

import view.units.AnimatedUnit;

/**
 * @author Bilal Hussain
 */
public  abstract class UnitStatee<E> {

	abstract void exec(GuiMap map, AnimatedUnit u, E arg);
	
	class Waiting extends UnitStatee<IsoTile>{
		@Override
		void exec(GuiMap map, AnimatedUnit u, IsoTile arg) {
			
		}
	}
	
}
