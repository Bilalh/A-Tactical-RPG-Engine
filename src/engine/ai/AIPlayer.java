package engine.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import util.Logf;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;

import engine.map.IMutableMapUnit;
import engine.map.MapPlayer;
/**
 * @author bilalh
 */
public class AIPlayer extends MapPlayer {
	private static final Logger log = Logger.getLogger(AIPlayer.class);
	
	private Map map;
	private PriorityQueue<IMutableMapUnit> lowestHp;
	
	
	public AIPlayer(Map map, ArrayList<IMutableMapUnit> units){
		super(units);
		this.map = map;
		lowestHp = new PriorityQueue<IMutableMapUnit>(16, new Comparator<IMutableMapUnit>() {
			@Override
			public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
				 int r = o1.getCurrentHp() - o2.getCurrentHp();
				 if (r == 0) r = o1.getStrength() - o2.getStrength();
				 return -r;
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
				Logf.info(log, "l %s", l);
				chosen = l;
				currentDistance = dist;
			}
		}
		
		return chosen;
	}
	
}
