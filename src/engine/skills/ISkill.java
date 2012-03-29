package engine.skills;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;
import common.interfaces.Identifiable;

/**
 * Inferface for a skill
 * @author Bilal Hussain
 */
@XStreamAlias("skill")
public interface ISkill extends Identifiable {

	Collection<Location> getAttackRange(Location start, int width, int height);

	Collection<Location> getArea(Location start, int width, int height);

	boolean isTargetOpposite();

	void setTargetOpposite(boolean targetOpposite);
	
	boolean isIncludeCaster();
	
	void setIncludeCaster(boolean includeCaster);
	
	String getName();
	
	void setName(String name);
	
	int getPower();

	void setPower(int power);
	 
	 
	
	
}