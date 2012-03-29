package common.interfaces;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;
import engine.map.Map;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("weapon")
public interface IWeapon extends Identifiable {

	Collection<Location> getAttackRange(Location start, int width, int height);

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