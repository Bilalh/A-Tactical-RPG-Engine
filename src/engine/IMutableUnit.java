package engine;

import common.interfaces.IUnit;

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

}