package engine.pathfinding;

import java.util.*;

import org.apache.log4j.Logger;

import util.Util;

import common.Location;

import config.Args;
import config.Logf;
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
	
	// Cache Calcuted paths
	HashMap<LocationInfo, Deque<LocationInfo>> paths = new HashMap<LocationInfo, Deque<LocationInfo>>();

	public PathFinder(IModelUnit u, IMap map) {
		Args.nullCheck(u,map);
		this.unit = u;
		this.map = map;
		d = new Dijkstra(this, map.getFieldWidth(), map.getFieldHeight());
		Location p = unit.getLocation();
		start = p.copy().translate(-unit.getMove()+1).limitLower(0, 0);
		end   = p.copy().translate(unit.getMove()+1).limitUpper(map.getFieldWidth(), map.getFieldHeight());
		Logf.trace(log, "start:%s, start.x:%s, end.x:%s, start.y:%s, end.y:%s",start, start.x, end.x, start.y, end.y);
		locations = d.calculate(u.getLocation(), start.x, end.x, start.y, end.y);
		
		Logf.debug(log,"locations for %s: %s\n",u, Util.array2d(locations, start.x, end.x, start.y, end.y, true));
		
	}

	/** Get all Locations that are vaild*/
	public ArrayList<LocationInfo> getMovementRange(){
		if (inRange != null) return inRange;

		inRange = new ArrayList<LocationInfo>();
		for (int i = start.x; i < end.x; i++) {
			for (int j = start.x; j < end.y; j++) {
				if (locations[i][j].getMinDistance() <= unit.getMove()) {
					inRange.add(locations[i][j]);
				}
			}
		}
		return inRange;
	}
	
	/**
	 * Get the path to a specifed Location
	 * @return The path as a list or null if the location is not reachable with the unit. 
	 */
	public Deque<LocationInfo> getMovementPath(Location p){
		Args.nullCheck(p);
		Args.validateRange(p.x, start.x, end.x);
		Args.validateRange(p.y, start.y, end.y);
		Logf.info(log, "Finding path to %s for %s", p, unit);
		
		if (locations[p.x][p.y] == null || locations[p.x][p.y].getMinDistance() > unit.getMove()){
			return null;
		}
		
		final LocationInfo pl= locations[p.x][p.y];
		
		Deque<LocationInfo> maybePath =  paths.get(pl);
		if (maybePath != null) return maybePath;
		
		Deque<LocationInfo> path = new ArrayDeque<LocationInfo>();
		
		for(LocationInfo l = pl; l != locations[unit.getGridX()][unit.getGridY()];l = l.getPrevious()){
			path.push(l);
			log.trace("path " + path);
		}
		
		path.push(locations[unit.getGridX()][unit.getGridY()]);
		log.info("finalpath " + path);
		paths.put(pl,path);
		return path;
		
	}
	
	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return unit.getCost(map.getTile(x, y), map.getTile(newX, newY));
	}

}
