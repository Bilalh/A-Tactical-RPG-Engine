package engine.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import common.Location;
import common.enums.Direction;

/**
 * @author Bilal Hussain
 */
public class MeleeWeapon extends AbstractWeapon {

	public MeleeWeapon() {
		this.range = 1;
		this.imageRef="0-12";
	}

	public MeleeWeapon(int strength) {
		this();
		this.strength = strength;
	}

	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {

		ArrayList<Location> list = new ArrayList<Location>();
		for (Direction  d : Direction.values()) {
			if (d == Direction.STILL) continue;
			Location t = start.copy().translate(d.x, d.y);
			if (t.x >= 0 && t.x < width && t.y >= 0 && t.y < height) {
				list.add(t);
			}
		}
		return list;
	}

	@Override
	public String getDetails() {
		return "1";
	}

	
	// Melee Weapon range is always one.
	@Override
	public void setRange(int range) {
		
	}
	
}
