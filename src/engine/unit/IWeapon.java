package engine.unit;

import java.util.Collection;

import common.Location;
import engine.map.interfaces.IMutableMapUnit;

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

	Collection<IMutableMapUnit> getTarget(IMutableMapUnit attacker, IMutableMapUnit target);
	
}