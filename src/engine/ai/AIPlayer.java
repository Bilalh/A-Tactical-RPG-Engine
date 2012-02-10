package engine.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;

import engine.map.IMutableMapUnit;
import engine.map.MapPlayer;
/**
 * @author bilalh
 */
public class AIPlayer extends MapPlayer {

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
		
		double currentDistance = Double.MAX_VALUE;
		for (LocationInfo l : movementRange) {
			if (tl.adjacent(l)){
				return l;
			}
			double dist =  tl.distance(l);
			if (dist < currentDistance){
				chosen = l;
				currentDistance = dist;
			}
		}
		
		return chosen;
	}

	public static void main(String[] args) {
		Location a = new Location(0, 1);
		Location b = new Location(1, 1);
		System.out.println(Location.distance(a, b));
	}
	
}
