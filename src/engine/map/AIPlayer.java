package engine.map;

import java.util.*;

import org.apache.log4j.Logger;

import util.Logf;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;

import engine.map.ai.AbstractAIBehaviour;
import engine.map.ai.LowestHp;
import engine.map.interfaces.IMutableMapUnit;
/**
 * @author bilalh
 */
public class AIPlayer extends MapPlayer {
	private static final Logger log = Logger.getLogger(AIPlayer.class);
	
	private Map map;
	private MapPlayer player;
	
	private PriorityQueue<IMutableMapUnit> ordering;
	private AbstractAIBehaviour         comparator;
	
	public AIPlayer(Map map, MapPlayer player){
		this.map    = map;
		this.player = player;
		
		comparator = new LowestHp(map, this, player);
		ordering   = new PriorityQueue<IMutableMapUnit>(16,comparator);
	}
	 
	@Override
	public void setUnits(ArrayList<IMutableMapUnit> units){
		super.setUnits(units);
	}
	
	public ILocation getMoveLocation(AIMapUnit a){
		ordering = new PriorityQueue<IMutableMapUnit>(16,a.getBehaviour());
		ordering.addAll(player.getUnits());
		
		IMutableMapUnit target = ordering.remove();
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
			// since we allready found a adjacent unit
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
		ordering.remove(u);
	}
	
}
