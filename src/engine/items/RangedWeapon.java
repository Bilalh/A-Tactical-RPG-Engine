package engine.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import util.ArrayUtil;

import common.Location;
import engine.unit.IWeapon;

/**
 * @author Bilal Hussain
 */
public class RangedWeapon extends AbstractWeapon {

	int innerRange = 1;
	
	public RangedWeapon() {}
	
	public RangedWeapon(int strength, int outerRange, int innerRange) {
		this.strength   = strength;
		this.range      = outerRange;
		this.innerRange = innerRange;
	}

	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {
		HashSet<Location> set = makeRange(start, width, height, range);
		set.removeAll(makeRange(start, width, height, innerRange));
		set.remove(start);
		return set;
	}

	private HashSet<Location> makeRange(Location start, int width, int height, int range){
		HashSet<Location> set = new HashSet<Location>();
		for (int i = range; i > 0; i--) {
			for (int j = -1; j <= 1; j += 2) {
				final int diff = start.y + j * (range - i);
				if (diff >= height || diff < 0) continue;

				Location m = start.copy().translate(0, j * (range - i));
				set.add(m);
				for (int k = 1; k <= i; k++) {
					if (m.x + k < width) set.add(m.copy().translate(k, 0));
					if (m.x - k >= 0) set.add(m.copy().translate(-k, 0));
				}
			}
		}

		if (start.y + range < height) set.add(start.copy().translate(0, range));
		if (start.y - range >= 0) set.add(start.copy().translate(0, -range));
		return set;
	}
	
	@Override
	public String getDetails() {
		return innerRange + "-" + range;
	}

	/** @category Generated */
	public int getInnerRange() {
		return innerRange;
	}

	/** @category Generated */
	public void setInnerRange(int innerRange) {
		this.innerRange = innerRange;
	}

	public static void main(String[] args) {
		int width = 10, height = 15;
		IWeapon w = new RangedWeapon(10, 5,3);
		Collection<Location> c = w.getAttackRange(new Location(3, 8), width, height);
		// System.out.println(c);
	
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (c.contains(new Location(i, j))) {
					System.out.print("+");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
	
		char[][] arr = new char[width][height];
		for (int i = 0; i < arr.length; i++) {
			Arrays.fill(arr[i], ' ');
		}
		for (Location l : c) {
			arr[l.x][l.y] = '+';
		}
		System.out.println(ArrayUtil.numberedArray2d(arr));
	}

}
