package engine.skills;

import java.util.Collection;

import common.Location;

/**
 * @author Bilal Hussain
 */
public interface ISkill {

	Collection<Location> getAttackRange(Location start, int width, int height);

	Collection<Location> getArea(Location start, int width, int height);

	/** @category Generated */
	String getName();

	/** @category Generated */
	void setName(String name);

	/** @category Generated */
	int getPower();

	/** @category Generated */
	void setPower(int power);

	/** @category Generated */
	int getRange();

	/** @category Generated */
	void setRange(int range);

	/** @category Generated */
	boolean isTargetOpposite();

	/** @category Generated */
	void setTargetOpposite(boolean targetOpposite);

}