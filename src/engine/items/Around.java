package engine.items;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import util.Logf;

import common.Location;
import common.enums.Direction;
import engine.map.Map;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class Around extends AbstractWeapon {
	private static final Logger log = Logger.getLogger(Spear.class);
	
	public Around() {
		this.imageRef="3-15";
		this.range = 1;
	}


	public Around(int strength) {
		this();
		this.strength = strength;
	}

	private static final int[][] dirs = {
		{ 0, 1 }, 
		{ 1, 0 },
		
		{  0,-1 },
		{ -1, 0 },
		
		{  1,-1 }, 
		{ -1, 1 },
		
		{  1,  1},
		{ -1, -1},
		
	};
	
	@Override
	public Collection<Location> getAttackRange(Location l, int width, int height) {
		ArrayList<Location> list = new ArrayList<Location>();
		for (int[] d : dirs) {
				Location t = new Location(d[0], d[1]).translate(l.x, l.y);
				if (t.x >= 0 && t.x < width && t.y >= 0 && t.y < height) {
					list.add(t);
				}
		}
		
		assert !list.contains(l);
		return list;
	}

	@Override
	public Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, IMutableMapUnit target, Map map) {
		ArrayList<IMutableMapUnit> list = new ArrayList<IMutableMapUnit>();
		Location l    = attacker.getLocation();
		for (int[] d : dirs) {
			Location t = new Location(d[0], d[1]).translate(l.x, l.y);
			if (t.x >= 0 && t.x < map.getFieldWidth() && t.y >= 0 && t.y < map.getFieldHeight()) {
				IMutableMapUnit m =  map.getTile(t).getCurrentUnit();
				if (m != null) list.add(m);
			}
		}
		Logf.debug(log, "targets: ", list);
		return list;
	}
	
	@Override
	public String getDetails() {
		return "1(Around)";
	}

	@Override
	public String toString() {
		return String.format("Spear [strength=%s, range=%s, imageRef=%s, name=%s, uuid=%s]", strength, range, imageRef, name, uuid);
	}

	@Override
	public void setRange(int range) {
		// Range is always 1
	}

}
