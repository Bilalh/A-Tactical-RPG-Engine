package engine.unit;

import java.util.Collection;
import java.util.HashSet;

import common.Location;

/**
 * @author Bilal Hussain
 */
public class Skill {

	protected String name;
	protected int power;
	protected int range;

	protected int area; // Number of squares affected
	
	public Skill(){
	}
	
	public Skill(String name, int power, int range, int area) {
		this();
		this.name  = name;
		this.power = power;
		this.range = range;
		this.area  = area;
	}

	public Collection<Location> getAttackRange(Location start, int width, int height) {
		HashSet<Location> set = makeRange(start, width, height, range);
		return set;
	}
	
	public Collection<Location> getArea(Location start, int width, int height){
		Collection<Location> results =  makeRange(start, width, height, area);
		results.add(start);
		return results;
	}
	
	/**
	 * Makes a diamend shaped range around a specifed point.
	 * 
	 * <pre>
	 *    +
	 *   +++
	 *  ++S++
	 *   +++
	 *    +
	 * </pre>
	 * 
	 * Above shows the result for range of 2 around the start..
	 * 
	 * The result does <b>not</b> contain the starting point. 
	 * 
	 */
	protected HashSet<Location> makeRange(Location start, int width, int height, int range){
		HashSet<Location> set = new HashSet<Location>();
		
		for (int i = range; i > 0; i--) {
			for (int j = -1; j <= 1; j += 2) {
				final int diff = start.y + j * (range - i);
				if (diff >= height || diff < 0) continue;

				Location m = start.copy().translate(0, j * (range - i));
				set.add(m);
				for (int k = 1; k <= i; k++) {
					if (m.x + k < width) set.add(m.copy().translate(k, 0));
					if (m.x - k >= 0)    set.add(m.copy().translate(-k, 0));
				}
			}
		}

		if (start.y + range < height) set.add(start.copy().translate(0, range));
		if (start.y - range >= 0)     set.add(start.copy().translate(0, -range));
		return set;
	}

	/** @category Generated */
	public String getName() {
		return name;
	}

	/** @category Generated */
	public void setName(String name) {
		this.name = name;
	}

	/** @category Generated */
	public int getPower() {
		return power;
	}

	/** @category Generated */
	public void setPower(int power) {
		this.power = power;
	}

	/** @category Generated */
	public int getRange() {
		return range;
	}

	/** @category Generated */
	public void setRange(int range) {
		this.range = range;
	}
	
}
