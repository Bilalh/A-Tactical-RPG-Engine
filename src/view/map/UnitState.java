package view.map;

import java.util.Collection;

import org.apache.log4j.Logger;

import common.LocationInfo;

import util.Logf;
import view.map.IsoTile.TileState;
import view.ui.Menu;
import view.units.AnimatedUnit;

/**
 * State Machine for a Unit
 * @author Bilal Hussain
 */
enum UnitState {
	
	WAITING {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return MOVEMENT_RANGE;
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			map.waitingCancel();
			return this;
		}
		
	},
	MENU_SELECTED {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			// SetupMenu
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			// display menu?
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			
		}
	},
	MOVEMENT_RANGE {
		private Collection<LocationInfo> inRange = null;

		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			
			// Set up movement range
			inRange =  map.getMapController().getMovementRange(map.getCurrentUnit().getUnit());
			for (LocationInfo p : inRange) {
				map.getTile(p).setState(TileState.MOVEMENT_RANGE);
			}
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			assert other != null;
			assert tile != null;
			
			if (!map.getSelectedTile().isSelected()) return this;

			if (!inRange.contains(map.getSelectedTile().getLocation())) {
				Logf.info(log, "%s not in range", map.getSelectedTile());
				return this;
			}

			map.getMapController().moveUnit(other.getUnit(), map.getSelectedTile().getLocation());
			for (LocationInfo p : inRange) {
				map.getTile(p).setState(TileState.NONE);
			}
			inRange = null;
			log.info("Selected unit move finished");
			
			return MENU_MOVED;
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			map.removeRange(inRange);
			return WAITING; 
		}
		
	},
	MENU_MOVED {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			// Setup menu
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			
		}
	},
	SHOW_TARGETS {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			// Show Range
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			// Remove Range
		}
	},
	FIGHT {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			
		}
	},
	FINISHED {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			
		}
	},
	DEFEATED {
		@Override
		void stateEntered(GuiMap map, AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile) {
			return null;
			
		}

		@Override
		UnitState cancel(GuiMap map, AnimatedUnit other) {
			return null;
			
		}

	};

	private static final Logger log = Logger.getLogger(UnitState.class);
	
	abstract void stateEntered(GuiMap map, AnimatedUnit other);

	abstract UnitState exec(GuiMap map, AnimatedUnit other, IsoTile tile);

	abstract UnitState cancel(GuiMap map, AnimatedUnit other);
}