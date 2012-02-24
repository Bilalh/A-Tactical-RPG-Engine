package common.interfaces;

import java.util.ArrayList;

import engine.map.Tile;
import engine.skills.ISkill;
import engine.skills.RangedSkill;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public interface IUnit extends Identifiable {

	int getMaxHp();

	int getMove();

	int getStrength();

	String getName();

	int getExp();

	int getLevel();

	int getDefence();

	int getSpeed();
	
	int getCost(Tile old, Tile next);

	UnitImages getImageData();
	
	IWeapon getWeapon();

	ArrayList<ISkill> getSkills();
	
}