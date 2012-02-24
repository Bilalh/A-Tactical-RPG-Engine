package engine.skills;

import java.util.Collection;

import common.Location;

/**
 * @author Bilal Hussain
 */
public interface ISkill {

	Collection<Location> getAttackRange(Location start, int width, int height);

	Collection<Location> getArea(Location start, int width, int height);

	String getName();

	int getPower();

	boolean isTargetOpposite();

	boolean isIncludeCaster();
	
}