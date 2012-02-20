package view.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import common.Location;
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
		
	// See docs/Unit.pdf for a diagram
	
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
		private final List<MenuItem> commands = Arrays.asList(new MenuItem[]{
				new MenuItem("Move"), new MenuItem("Attack"), new MenuItem("Wait"), new MenuItem("Cancel")
		});
		
		@Override
		void stateEntered(AnimatedUnit other) {
			map.getMenu().setCommands(commands);
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			int index = map.getMenu().getSelectedIndex();

			switch (index) {
				case 0:
					return MOVEMENT_RANGE;
				case 2:
					return FINISHED;
				case 3:
					return cancel(null);
				default:
					return this;
			}
			
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
			inRange =  map.highlightRange(map.getCurrentUnit(), TileState.MOVEMENT_RANGE);
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
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
			map.removeRange(inRange);
			
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
		private final List<MenuItem> commands = Arrays.asList(new MenuItem[]{
				new MenuItem("Attack"), new MenuItem("Wait")
		});
		
		@Override
		void stateEntered(AnimatedUnit other) {
			map.getMenu().setCommands(commands); 
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			int index = map.getMenu().getSelectedIndex();

			switch (index) {
				case 0:
					return SHOW_TARGETS;
				case 1:
					return FINISHED;
				default:
					return this;
			}
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			return this;
			
		}
	},
	
	SHOW_TARGETS {
		private Collection<Location> targets = null;

		@Override
		void stateEntered(AnimatedUnit other) {
			targets = map.getMapController().getVaildTargets(map.getCurrentUnit().getUnit());
			for (Location p : targets) {
				map.getTile(p).setState(TileState.ATTACK_RANGE);
			}
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			return this;
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			map.removeRange(targets);
			return MENU_MOVED;
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
			map.getMapController().unitTurnFinished(map.getCurrentUnit().getUnit());
		}

		@Override
		UnitState exec(AnimatedUnit other, IsoTile otherTile) {
			assert false : "Should not be called";
			return WAITING;
			
		}

		@Override
		UnitState cancel(AnimatedUnit other) {
			assert false : "Should not be called";
			return this;
			
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
	
	/** Peforms any actions when the state is entered */
	abstract void stateEntered(AnimatedUnit other);
	/** Peform any actions of the state and return the next state */
	abstract UnitState exec(AnimatedUnit other, IsoTile otherTile);
	/** Goes to the previous state */
	abstract UnitState cancel(AnimatedUnit other);
	
	private static GuiMap map;
	public static void setMap(GuiMap map) {UnitState.map = map;}

	private static final Logger log = Logger.getLogger(UnitState.class);
}
