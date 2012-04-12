package common.interfaces;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;
import engine.map.Map;
import engine.map.interfaces.IMutableMapUnit;

/**
 * Interface for a weapon
 * @author Bilal Hussain
 */
@XStreamAlias("weapon")
public interface IWeapon extends Identifiable {

	/**
	 * Gets the attack range of the weapon.
	 *
	 * @param start the start point
	 * @param width the width of the map
	 * @param height the height of the map
	 * @return the attack range
	 */
	Collection<Location> getAttackRange(Location start, int width, int height);

	/**
	 * Gets all the targets
	 *
	 * @param attacker the attacker
	 * @param target the first target
	 * @param map the map
	 * @return the targets
	 */
	Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, IMutableMapUnit target, Map map);
	
	String getDetails();

	int getStrength();

	void setStrength(int strength);
	
	int getRange();

	void setRange(int range);

	String getImageRef();

	void setImageRef(String imageRef);

	String getName();
	
	void setName(String name);
	
}