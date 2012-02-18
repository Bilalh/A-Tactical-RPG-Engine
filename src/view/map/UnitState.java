package view.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import common.LocationInfo;

import util.Logf;
import view.map.IsoTile.TileState;
import view.map.UnitStatee.Waiting;
import view.ui.Menu;
import view.ui.MenuItem;
import view.units.AnimatedUnit;

/**
 * State Machine for a Unit
 * @author Bilal Hussain
 */
enum UnitState {
	
	WAITING {
		@Override
		void stateEntered(AnimatedUnit other) {
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return MENU_SELECTED;
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			map.waitingCancel();
			return this;
		}
		
	},
	MENU_SELECTED {
		private List<MenuItem> commands = Arrays.asList(new MenuItem[]{
				new MenuItem("Attack"), new MenuItem("Wait"), new MenuItem("item"), new MenuItem("Back")}
		);
		
		@Override
		void stateEntered(AnimatedUnit other) {
			map.getMenu().setCommands(commands);
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return MOVEMENT_RANGE;
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return WAITING;
			
		}
	},
	
	MOVEMENT_RANGE {
		private Collection<LocationInfo> inRange = null;

		@Override
		void stateEntered(AnimatedUnit other) {
			
			// Set up movement range
			inRange =  map.getMapController().getMovementRange(map.getCurrentUnit().getUnit());
			for (LocationInfo p : inRange) {
				map.getTile(p).setState(TileState.MOVEMENT_RANGE);
			}
			
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			assert other != null;
			assert otherTile != null;
			
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
		UnitState cancel(AnimatedUnit other) {
			map.removeRange(inRange);
			return MENU_SELECTED; 
		}
		
	},
	
	MENU_MOVED {
		@Override
		void stateEntered(AnimatedUnit other) {
			// Setup menu
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return null;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return null;
			
		}
	},
	
	SHOW_TARGETS {
		@Override
		void stateEntered(AnimatedUnit other) {
			// Show Range
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return null;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return null;
			// Remove Range
		}
	},
	
	FIGHT {
		@Override
		void stateEntered(AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return null;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return null;
			
		}
	},
	
	FINISHED {
		@Override
		void stateEntered(AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return null;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return null;
			
		}
	},
	
	DEFEATED {
		@Override
		void stateEntered(AnimatedUnit other) {
			
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return null;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return null;
			
		}

	};

	abstract void      stateEntered(AnimatedUnit other);
	abstract UnitState exec(AnimatedUnit other, IsoTile otherTile);
	abstract UnitState cancel(AnimatedUnit other);
	
	private static GuiMap map;
	public static void setMap(GuiMap map) {UnitState.map = map;}

	private static final Logger log = Logger.getLogger(UnitState.class);
}
