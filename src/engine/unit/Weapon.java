package engine.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import util.ArrayUtil;

import common.Location;

/**
 * @author Bilal Hussain
 */
public class Weapon implements IWeapon {

	protected int strength;
	protected int range;

	public Weapon() {}
	
	public Weapon(int strength, int range) {
		this.strength = strength;
		this.range = range;
	}

	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {

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

		set.remove(start);

		return set;
	}

	@Override
	public String getDetails() {
		return "" + range;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	public static void main(String[] args) {
		int width = 9, height = 15;
		IWeapon w = new Weapon(10, 6);
		Collection<Location> c = w.getAttackRange(new Location(4, 4), width, height);
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
	public void setStrength(int strength) {
		this.strength = strength;
	}

}
