package engine.skills;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("rangedSkill")
public class RangedSkill extends AbstractSkill {

	protected int range;

	protected int numberOfTilesEffected; // Number of squares affected
	protected Location startLocation;
	
	public RangedSkill(String name, int power, int range, int area, boolean targetOpposite, boolean includeCaster) {
		super();
		this.name  = name;
		this.power = power;
		this.range = range;
		this.numberOfTilesEffected  = area;
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
		HashSet<Location> results =  makeRange(start, width, height, numberOfTilesEffected);
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
	public int getNumberOfTilesEffected() {
		return numberOfTilesEffected;
	}

	/** @category Generated */
	public void setNumberOfTilesEffected(int area) {
		this.numberOfTilesEffected = area;
	}
	
}
