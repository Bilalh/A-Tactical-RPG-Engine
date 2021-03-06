package view.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import view.map.IsoTile.TileState;

import common.Location;
import engine.skills.ISkill;

/**
 * Shows the area of effect of the skill as well as its range.
 * @author Bilal Hussain
 */
public class SkillMovement extends Movement {

	ISkill skill;
	Collection<Location> skillArea   = new ArrayList<Location>();
	HashSet<Location> attackRange;
	
	public SkillMovement(GuiMap map) {
		super(map);
	}
	
	public void showSkillArea(){
		for (Location l : skillArea) {
			map.getTile(l).setState(TileState.NONE);
		}
		
		for (Location l : attackRange) {
			map.getTile(l).setState(TileState.ATTACK_RANGE);
		}
		
		if (!attackRange.contains(map.getSelectedTile().getLocation())) return;

		skillArea = skill.getArea(map.getSelectedTile().getLocation(), map.getFieldWidth(), map.getFieldHeight());
		for (Location l : skillArea) {
			map.getTile(l).setState(TileState.ATTACK_AREA);
		}		
	}
	
	@Override
	public void keyUp() {
		super.keyUp();
		showSkillArea();
	}

	@Override
	public void keyDown() {
		super.keyDown();
		showSkillArea();
	}

	@Override
	public void keyLeft() {
		super.keyLeft();
		showSkillArea();
	}

	@Override
	public void keyRight() {
		super.keyRight();
		showSkillArea();
	}

	public void setSkill(ISkill skill) {
		this.skill = skill;
	}


	public void setAttackRange(Collection<Location> attackRange) {
		this.attackRange = new HashSet<Location>(attackRange);
	}


	public Collection<Location> getSkillArea() {
		return skillArea;
	}
	
}
