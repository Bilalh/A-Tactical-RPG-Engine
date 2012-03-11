package engine.unit;

import java.util.ArrayList;

import common.interfaces.IUnit;
import common.interfaces.IWeapon;
import engine.map.MapPlayer;
import engine.skills.ISkill;
import engine.skills.RangedSkill;

/**
 * A unit has a number of attributes
 * @author Bilal Hussain
 */
public interface IMutableUnit extends IUnit {

	void setMaxHp(int maxHp);

	void setMove(int move);

	void setStrength(int strength);

	void setDefence(int defence);

	void setLevel(int level);

	void setExp(int exp);

	void setSpeed(int speed);

	void setName(String name);

	void setWeapon(IWeapon weapon);
	
	void setSkills(ArrayList<ISkill> skills);

	void setImageData(String ref, SpriteSheetData imageData);
	
}