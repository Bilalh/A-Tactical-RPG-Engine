package engine.skills;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;

/**
 * A skill that has a range about the unit
 * @author Bilal Hussain
 */
public class RangedSkill extends AbstractSkill {

	protected int range;

	protected int area; // Number of squares affected
	protected Location startLocation;
	
	public RangedSkill(){
		range = 1;
	}
	
	public RangedSkill(String name, int power, int range, int area, boolean targetOpposite, boolean includeCaster) {
		this();
		this.name  = name;
		this.power = power;
		this.range = range;
		this.area  = area;
		this.targetOpposite = targetOpposite;
		this.includeCaster = includeCaster;
	}


	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {
		startLocation = start;
		HashSet<Location> results = makeRange(start, width, height, range);
		if (!includeCaster) results.remove(startLocation);
		return results;
	}
	
	@Override
	public Collection<Location> getArea(Location start, int width, int height){
		HashSet<Location> results =  makeRange(start, width, height, area);
		if (!includeCaster) results.remove(startLocation);
		return results;
	}
	
	/** @category Generated */
	public int getRange() {
		return range;
	}

	/** @category Generated */
	public void setRange(int range) {
		this.range = range;
	}

	/** @category Generated */
	public int getArea() {
		return area;
	}

	/** @category Generated */
	public void setArea(int area) {
		this.area = area;
	}
	
}
