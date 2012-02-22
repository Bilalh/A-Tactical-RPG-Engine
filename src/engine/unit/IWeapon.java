package engine.unit;

import java.util.Collection;

import common.Location;

/**
 * @author Bilal Hussain
 */
public interface IWeapon {

	Collection<Location> getAttackRange(Location start, int width, int height);

	String getDetails();

	int getStrength();

	void setStrength(int strength);
	
	int getRange();

	void setRange(int range);

}