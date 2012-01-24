package engine.pathfinding;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import common.Location;

import config.LogF;
import engine.map.IMap;
import engine.map.IModelUnit;


/**
 * @author Bilal Hussain
 */
public class PathFinder implements IMovementCostProvider {

    private static Logger log = Logger.getLogger(PathFinder.class);
	
    private Dijkstra d;
	private IModelUnit unit;
	private IMap map;
	
	private LocationInfo[][] locations;
	Location start;
	Location end;
	
	private ArrayList<LocationInfo> inRange;
	
	public PathFinder(IModelUnit u, IMap map) {
		this.unit = u;
		this.map = map;
		d = new Dijkstra(this, map.getFieldWidth(), map.getFieldHeight());
		Location p = unit.getLocation();
		start = p.copy().translate(-unit.getMove()).limitLower(0, 0);
		end   = p.copy().translate(unit.getMove()).limitUpper(map.getFieldWidth(), map.getFieldHeight());
		LogF.trace(log, "start:%s, start.x:%s, end.x:%s, start.x:%s, end.y:%s",start, start.x, end.x, end.x, end.y);
		locations = d.calculate(start, start.x, end.x, start.x, end.y);
	}
	
	public ArrayList<LocationInfo> getMovementRange(){
		if (inRange == null){
			inRange = new ArrayList<LocationInfo>();
			for (int i = start.x; i < end.x; i++) {
				for (int j = start.x; j < end.y; j++) {
					if (locations[i][j].getMinDistance() <= unit.getMove()){
						inRange.add(locations[i][j]);
					}
				}
			}
		}
		
		return inRange;
	}
	
	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return unit.getCost(map.getTile(x, y), map.getTile(newX, newY));
	}

}
