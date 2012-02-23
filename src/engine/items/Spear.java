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
public class Spear extends AbstractWeapon {
	private static final Logger log = Logger.getLogger(Spear.class);
	
	public Spear() {
	}

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
	public Collection<IMutableMapUnit> getTarget(IMutableMapUnit attacker, IMutableMapUnit target, Map map) {
		ArrayList<IMutableMapUnit> list = new ArrayList<IMutableMapUnit>();
		Location diff = target.getLocation().copy().sub(attacker.getLocation()).limitUpper(1, 1).limitLower(-1, -1);
		Location t    = attacker.getLocation().copy().translate(diff.x * range, diff.y * range);
		
		Logf.debug(log, "Attacker:%s", attacker);
		Logf.debug(log, "target:%s",    target);
		Logf.debug(log, "diff:%s",    diff);
		Logf.debug(log, "t:%s",    t);
		
		while(!t.equals(attacker.getLocation())){
			IMutableMapUnit u =  map.getTile(t).getCurrentUnit();
			log.debug("checking from " + t + ":" + u);
			if (u != null){
				list.add(u);
			}
			t.sub(diff);
		}
		Logf.debug(log, "targets: ", list);
		return list;
	}
	
	@Override
	public String getDetails() {
		return "1-" + range;
	}

}
