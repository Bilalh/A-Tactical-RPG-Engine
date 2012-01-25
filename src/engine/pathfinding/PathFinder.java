package engine.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;

import common.Location;

import config.Args;
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
		Args.nullCheck(u,map);
		this.unit = u;
		this.map = map;
		d = new Dijkstra(this, map.getFieldWidth(), map.getFieldHeight());
		Location p = unit.getLocation();
		start = p.copy().translate(-unit.getMove()+1).limitLower(0, 0);
		end   = p.copy().translate(unit.getMove()+1).limitUpper(map.getFieldWidth(), map.getFieldHeight());
		LogF.trace(log, "start:%s, start.x:%s, end.x:%s, start.y:%s, end.y:%s",start, start.x, end.x, start.y, end.y);
		locations = d.calculate(u.getLocation(), start.x, end.x, start.y, end.y);
		
		LogF.debug(log,"locations for %s: %s\n",u, LogF.array2d(locations, start.x, end.x, start.y, end.y, true));
		
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
	
	// Cache Calcuted paths
	HashMap<LocationInfo, ArrayList<LocationInfo>> paths = new HashMap<LocationInfo, ArrayList<LocationInfo>>();
	
	/**
	 * Get the path to a specifed Location
	 * @return The path as a list or null if the location is not reachable with the unit. 
	 */
	public ArrayList<LocationInfo> getMovementPath(Location p){
		Args.nullCheck(p);
		Args.validateRange(p.x, start.x, end.x);
		Args.validateRange(p.y, start.y, end.y);
		
		if (locations[p.x][p.y] == null || locations[p.x][p.y].getMinDistance() > unit.getMove()){
			return null;
		}
		
		final LocationInfo pl= locations[p.x][p.y];
		
		ArrayList<LocationInfo> maybePath =  paths.get(pl);
		if (maybePath != null) return maybePath;
		
		
		ArrayList<LocationInfo> path = new ArrayList<LocationInfo>();
		for (LocationInfo l = locations[unit.getGridX()][unit.getGridY()]; l != pl;) {
			path.add(l);
			l = locations[l.x + l.getNextDirection().x][l.y + l.getNextDirection().y];
		}
		path.add(pl);
		paths.put(pl,path);
		return path;
		
	}
	
	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return unit.getCost(map.getTile(x, y), map.getTile(newX, newY));
	}

}
