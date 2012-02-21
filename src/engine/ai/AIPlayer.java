package engine.ai;

import java.util.*;

import org.apache.log4j.Logger;

import util.Logf;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;

import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;
/**
 * @author bilalh
 */
public class AIPlayer extends MapPlayer {
	private static final Logger log = Logger.getLogger(AIPlayer.class);
	
	private Map map;
	private PriorityQueue<IMutableMapUnit> lowestHp;
	
	
	public AIPlayer(Map map, ArrayList<IMutableMapUnit> units){
		this.units = units;
		this.map = map;
		lowestHp = new PriorityQueue<IMutableMapUnit>(16, new Comparator<IMutableMapUnit>() {
			@Override
			public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
				 int r = o1.getCurrentHp() - o2.getCurrentHp();
				 if (r == 0) r = o1.getStrength() - o2.getStrength();
				 return r;
			}
		});
	}
	
	
	public ILocation getMoveLocation(AIUnit a){
		lowestHp.clear();
		lowestHp.addAll(map.getPlayerUnits());
		
		IMutableMapUnit target = lowestHp.remove();
		Location tl = target.getLocation();
		
		Collection<LocationInfo> movementRange = map.getMovementRange(a);
		LocationInfo chosen = null;
		boolean adjacent = false;
		
		double currentDistance = Double.MAX_VALUE;
		for (LocationInfo l : movementRange) {
			
			double dist =  tl.distance(l);
			if (tl.adjacent(l)){
				if (!adjacent) {
					chosen = l;
					currentDistance = dist;
					adjacent = true;
				}else if (l.getMinDistance() < chosen.getMinDistance()){
					chosen = l;
					currentDistance = dist;
				}
			// since we allready found a adjacent
			}else if (!adjacent &&  dist < currentDistance){
				chosen = l;
				currentDistance = dist;
			}
		}
		
		Logf.info(log, "chosen %s", chosen);
		return chosen;
	}


	// Return the target the ai is going to attack or null if there is not one.
	public IMutableMapUnit getTarget(IMutableMapUnit u) {
		Collection<Location> targets =  map.getVaildTargets(u);
		if (targets.isEmpty()) return null;

		Iterator<Location> it   = targets.iterator();
		IMutableMapUnit current = map.getTile(it.next()).getCurrentUnit();
		
		while(it.hasNext()) {
			IMutableMapUnit v = map.getTile(it.next()).getCurrentUnit();
			if (v.getCurrentHp() < current.getCurrentHp()){
				current = v;
			}
		}
		return current;
	}


	@Override
	public void unitDied(IMutableMapUnit u) {
		super.unitDied(u);
		lowestHp.remove(u);
	}
	
}
