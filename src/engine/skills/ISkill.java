package engine.skills;

import java.util.Collection;

import common.Location;
import common.interfaces.Identifiable;

/**
 * @author Bilal Hussain
 */
public interface ISkill extends Identifiable {

	Collection<Location> getAttackRange(Location start, int width, int height);

	Collection<Location> getArea(Location start, int width, int height);

	String getName();

	int getPower();

	boolean isTargetOpposite();

	boolean isIncludeCaster();
	
}