package engine.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import util.ArrayUtil;

import common.Location;
import common.interfaces.IWeapon;

/**
 * Ranged combat
 * @author Bilal Hussain
 */
public class RangedWeapon extends AbstractWeapon {

	int innerRange = 1;
	
	public RangedWeapon() {
		this.imageRef="4-15";
	}
	
	public RangedWeapon(int strength, int outerRange, int innerRange) {
		this();
		this.strength   = strength;
		this.range      = outerRange;
		this.innerRange = innerRange;
	}

	@Override
	public Collection<Location> getAttackRange(Location start, int width, int height) {
		HashSet<Location> set = makeRange(start, width, height, range);
		set.removeAll(makeRange(start, width, height, innerRange));
		return boundCheck(set, width, height);
	}
	
	@Override
	public String getDetails() {
		return Math.max(innerRange,1) + "-" + range;
	}


	public int getInnerRange() {
		return innerRange;
	}


	public void setInnerRange(int innerRange) {
		this.innerRange = innerRange;
	}

	public static void main(String[] args) {
		int width = 10, height = 15;
		IWeapon w = new RangedWeapon(10, 3,1);
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

	@Override
	public String toString() {
		return String.format("RangedWeapon [innerRange=%s, strength=%s, range=%s, imageRef=%s, name=%s, uuid=%s]", innerRange, strength,
				range, imageRef, name, uuid);
	}

}
