package engine.skills;

import java.util.Collection;
import java.util.HashSet;

import common.Location;

/**
 * @author Bilal Hussain
 */
public class Skill implements ISkill {

	protected String name;
	protected int power;
	protected int range;

	protected int area; // Number of squares affected
	protected boolean targetOpposite; 
	
	public Skill(){
	}
	
	public Skill(String name, int power, int range, int area, boolean targetOpposite) {
		this();
		this.name  = name;
		this.power = power;
		this.range = range;
		this.area  = area;
		this.targetOpposite = targetOpposite;
	}

	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {
		HashSet<Location> set = makeRange(start, width, height, range);
		set.remove(start);
		return set;
	}
	
	@Override
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
	 * The result does contain the starting point. 
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
	@Override
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/** @category Generated */
	@Override
	public int getPower() {
		return power;
	}

	/** @category Generated */
	@Override
	public void setPower(int power) {
		this.power = power;
	}

	/** @category Generated */
	@Override
	public int getRange() {
		return range;
	}

	/** @category Generated */
	@Override
	public void setRange(int range) {
		this.range = range;
	}

	/** @category Generated */
	@Override
	public boolean isTargetOpposite() {
		return targetOpposite;
	}

	/** @category Generated */
	@Override
	public void setTargetOpposite(boolean targetOpposite) {
		this.targetOpposite = targetOpposite;
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
