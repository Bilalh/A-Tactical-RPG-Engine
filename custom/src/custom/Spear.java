package custom;

import java.util.ArrayList;
import java.util.Collection;

import common.Location;
import common.enums.Direction;

import engine.unit.IWeapon;

/**
 * @author Bilal Hussain
 */
public class Spear implements IWeapon {

	protected int strength;
	protected int range;

	public Spear(){}
	
	/** @category Generated */
	public Spear(int strength, int range) {
		this.strength = strength;
		this.range = range;
	}

	@Override
	public Collection<Location> getAttackRange(Location l, int width, int height) {

		ArrayList<Location> list = new ArrayList<Location>();

		for (Direction d : Direction.values()) {
			for (int i = 1; i <= range; i++) {
				Location t = new Location(d.x, d.y).mult(i).translate(l.x, l.y);
				if (t.x >= 0 && t.x < width && t.y >= 0 && t.y < height) {
					list.add(t);
				}
			}
		}
		return list;
	}

	@Override
	public String getDetails() {
		return "1-" + range;
	}

	/** @category Generated */
	@Override
	public int getStrength() {
		return strength;
	}

	/** @category Generated */
	@Override
	public void setStrength(int strength) {
		this.strength = strength;
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

}
