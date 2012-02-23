package view.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import common.Location;
import common.LocationInfo;
import engine.skills.Skill;

import util.Logf;
import view.map.GuiMap.ActionsEnum;
import view.map.IsoTile.TileState;
import view.ui.Menu;
import view.ui.MenuItem;
import view.ui.SkillMenuItem;
import view.units.AnimatedUnit;

/**
 * State Machine for a Unit
 * @author Bilal Hussain
 */
enum UnitState {
		
	// See docs/Unit.pdf for a diagram
	
	WAITING {
		@Override
		void stateEntered() {
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
		}

		@Override
		UnitState exec() {
			return MENU_SELECTED;
		}

		@Override
		UnitState cancel() {
			map.waitingCancel();
			return this;
		}
		
	},
	
	MENU_SELECTED {
		private final List<MenuItem> commands = Arrays.asList(new MenuItem[]{
				new MenuItem("Move"), new MenuItem("Attack"), new MenuItem("Skill"), new MenuItem("Wait")//, new MenuItem("Cancel")
		});
		
		@Override
		void stateEntered() {
			map.getMenu().setCommands(commands);
			map.getMenu().setWidth(80);
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec() {
			int index = map.getMenu().getSelectedIndex();

			switch (index) {
				case 0:
					return MOVEMENT_RANGE;
				case 1:
					return SHOW_ATTACK_TARGETS;
				case 2:
					return MENU_SKILL;
				case 3:
					return FINISHED;
//				case 4:
//					return cancel();
				default:
					return this;
			}
			
		}

		@Override
		UnitState cancel() {
			return WAITING;
			
		}
	},
	
	MOVEMENT_RANGE {
		private Collection<LocationInfo> inRange = null;

		@Override
		void stateEntered() {
			// Set up movement range
			inRange =  map.highlightRange(map.getCurrentUnit(), TileState.MOVEMENT_RANGE);
			assert inRange != null;
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
		}

		@Override
		UnitState exec() {
			assert map.getSelectedTile() != null;
			assert inRange != null;
			
			if (!map.getSelectedTile().isSelected()) return this;

			if (!inRange.contains(map.getSelectedTile().getLocation())) {
				Logf.info(log, "%s not in range", map.getSelectedTile());
				return this;
			}

			map.getMapController().moveUnit(map.getCurrentUnit().getUnit(), map.getSelectedTile().getLocation());
			map.removeRange(inRange);
			
			inRange = null;
			log.info("Selected unit move finished");
			
			return MENU_MOVED;
		}

		@Override
		UnitState cancel() {
			map.removeRange(inRange);
			return MENU_SELECTED; 
		}
		
	},
	
	MENU_MOVED {
		private final List<MenuItem> commands = Arrays.asList(new MenuItem[]{
				new MenuItem("Attack"), new MenuItem("Skill"), new MenuItem("Wait")
		});
		
		@Override
		void stateEntered() {
			map.getMenu().setCommands(commands); 
			map.getMenu().setWidth(80);
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec() {
			int index = map.getMenu().getSelectedIndex();

			switch (index) {
				case 0:
					return SHOW_ATTACK_TARGETS;
				case 1:
					return MENU_SKILL;
				case 2:
					return FINISHED;
				default:
					return this;
			}
		}

		@Override
		UnitState cancel() {
			return this;
			
		}
	},
	
	SHOW_ATTACK_TARGETS {
		Collection<Location> targets = null;

		@Override
		void stateEntered() {
			targets  = map.getCurrentUnit().getAttackRange(map.getFieldWidth(), map.getFieldHeight());
			
			for (Location p : targets) {
				map.getTile(p).setState(TileState.ATTACK_RANGE);
			}
			map.setActionHandler(GuiMap.ActionsEnum.MOVEMENT);
		}

		@Override
		UnitState exec() {
			IsoTile t =  map.getSelectedTile();
			AnimatedUnit au = t.getUnit();
			
			if (t.getState() != IsoTile.TileState.ATTACK_RANGE || au == null || map.getCurrentUnit().onSameTeam(au)) return null;
			map.removeRange(targets);
			return FIGHT_ATTACK;
		}

		@Override
		UnitState cancel() {
			map.removeRange(targets);
			return MENU_MOVED;
		}
	},
	
	FIGHT_ATTACK {
		@Override
		void stateEntered() {
			map.setActionHandler(ActionsEnum.NONE);
			map.getMapController().targetChosen(map.getCurrentUnit().getUnit(), map.getSelectedTile().getUnit().getUnit());
		}

		@Override
		UnitState exec() {
			assert false : "Should not be called";
			return FINISHED;
		}

		@Override
		UnitState cancel() {
			assert false : "Should not be called";
			return this;
			
		}
	},
	
	MENU_SKILL {

		private List<SkillMenuItem> commands;

		@Override
		void stateEntered() {
			commands = new ArrayList<SkillMenuItem>();

			for (Skill s : map.getCurrentUnit().getUnit().getSkills()) {
				commands.add(new SkillMenuItem(s));
			}
			map.getMenu().setCommands(commands);
			map.getMenu().setWidth(120);
			map.setActionHandler(GuiMap.ActionsEnum.MENU);
		}

		@Override
		UnitState exec() {
			int index = map.getMenu().getSelectedIndex();
			selectedSkill = commands.get(index).getSkill();
			
			return SHOW_SKILL_TARGETS;
		}

		@Override
		UnitState cancel() {
			return MENU_MOVED;
		}
	}, 
	
	SHOW_SKILL_TARGETS {
		Collection<Location> targets = null;
		@Override
		void stateEntered() {
			assert selectedSkill != null;
			targets = selectedSkill.getAttackRange(map.getCurrentUnit().getLocation(), map.getFieldWidth(), map.getFieldHeight());
			
			for (Location p : targets) {
				map.getTile(p).setState(TileState.ATTACK_RANGE);
			}
			map.getSkillMovement().setSkill(selectedSkill);
			map.getSkillMovement().setAttackRange(targets);
			map.getSkillMovement().showSkillArea();
			map.setActionHandler(GuiMap.ActionsEnum.SKILL_MOVEMENT);
		}

		@Override
		UnitState exec() {
			IsoTile t =  map.getSelectedTile();
			AnimatedUnit au = t.getUnit();
			
			if (t.getState() != IsoTile.TileState.ATTACK_AREA || au == null || map.getCurrentUnit().onSameTeam(au)  == selectedSkill.isTargetOpposite()) return null;
			map.removeRange(targets);
			map.removeRange(map.getSkillMovement().getSkillArea());
			return FIGHT_SKILL;
		}

		@Override
		UnitState cancel() {
			map.removeRange(targets);
			map.removeRange(map.getSkillMovement().getSkillArea());
			selectedSkill = null;
			return MENU_SKILL;
		}
		
	},
	
	FIGHT_SKILL {
		@Override
		void stateEntered() {
			map.setActionHandler(ActionsEnum.NONE);
			map.getMapController().skillTargetChosen(selectedSkill, map.getCurrentUnit().getUnit(), map.getSelectedTile().getLocation());
		}

		@Override
		UnitState exec() {
			assert false : "Should not be called";
			return FINISHED;
		}

		@Override
		UnitState cancel() {
			assert false : "Should not be called";
			return this;
			
		}
	},
	
	FINISHED {
		@Override
		void stateEntered() {
			map.getMapController().unitTurnFinished(map.getCurrentUnit().getUnit());
		}

		@Override
		UnitState exec() {
			assert false : "Should not be called";
			return WAITING;

		}

		@Override
		UnitState cancel() {
			assert false : "Should not be called";
			return this;

		}
	};
	
	/** Peforms any actions when the state is entered */
	abstract void stateEntered();
	/** Peform any actions of the state and return the next state */
	abstract UnitState exec();
	/** Goes to the previous state */
	abstract UnitState cancel();

	private static Skill selectedSkill;
	
	private static GuiMap map;
	public static void setMap(GuiMap map) {UnitState.map = map;}
	
	private static final Logger log = Logger.getLogger(UnitState.class);
}
