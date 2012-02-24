package engine.unit;

import java.util.ArrayList;

import common.interfaces.IUnit;
import common.interfaces.IWeapon;
import engine.map.MapPlayer;
import engine.skills.ISkill;
import engine.skills.RangedSkill;

/**
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

	int getMaxWeight();

	void setName(String name);

	void setWeapon(IWeapon weapon);
	
	void setSkills(ArrayList<ISkill> skills);

	void setImageData(String ref, UnitImages imageData);
	
}