package engine.pathfinding;

import java.util.*;

import org.apache.log4j.Logger;

import util.Args;
import util.Logf;
import util.ArrayUtil;

import common.Location;
import common.LocationInfo;
import common.ProxyLocationInfo;
import common.interfaces.IUnit;

import engine.map.IMap;
import engine.map.IMutableMapUnit;
import engine.unit.IMutableUnit;


/**
 * @author Bilal Hussain
 */
public class PathFinder implements IMovementCostProvider {

    private static Logger log = Logger.getLogger(PathFinder.class);
	
    private Dijkstra d;
	private IMutableMapUnit unit;
	private IMap map;
	
	private LocationInfo[][] locations;
	private Location start;
	private Location end;
	
	private HashSet<LocationInfo> inRange;
	
	// Cache Calcuted paths
	private HashMap<Location, ArrayDeque<LocationInfo>> paths = new HashMap<Location, ArrayDeque<LocationInfo>>();

	public PathFinder(IMutableMapUnit u, IMap map) {
		Args.nullCheck(u,map);
		this.unit = u;
		this.map = map;
		d = new Dijkstra(this, map.getFieldWidth(), map.getFieldHeight());
		Location p = unit.getLocation();
		start = p.copy().translate(-unit.getMove()+1).limitLower(0, 0);
		end   = p.copy().translate(unit.getMove()+1).limitUpper(map.getFieldWidth(), map.getFieldHeight());
		Logf.debug(log, "start:%s, start.x:%s, end.x:%s, start.y:%s, end.y:%s",start, start.x, end.x, start.y, end.y);
		locations = d.calculate(u.getLocation(), start.x, end.x, start.y, end.y);
		
		Logf.debug(log,"locations for %s: %s\n",u, ArrayUtil.array2d(locations, start.x, end.x, start.y, end.y, true));
		
	}

	/** Get all Locations that are vaild*/
	public Collection<LocationInfo> getMovementRange(){
		if (inRange != null) return inRange;

		Logf.debug(log,"u:%s start:%s end:%s \npf getMovementRange %s",unit, start,end, 
				ArrayUtil.array2d(locations, start.x,  end.x, start.y, end.y, true));
		
		inRange = new HashSet<LocationInfo>();
		for (int i = start.x; i < end.x; i++) {
			for (int j = start.y; j < end.y; j++) {
				IMutableMapUnit u = map.getTile(i, j).getCurrentUnit();
				if (u != null && u != unit) continue;
				
				if (locations[i][j].getMinDistance() <= unit.getMove()) {
					inRange.add(new ProxyLocationInfo(locations[i][j]));
//					inRange.add(locations[i][j]);
				}
			}
		}
		return Collections.unmodifiableCollection(inRange);
	}

	
	
	/**
	 * Get the path to a specifed Location
	 * @return The path as an immutable list or null if the location is not reachable with the unit. 
	 */
	public Collection<LocationInfo> getMovementPath(Location p){
		Args.nullCheck(p);
		Args.validateRange(p.x, start.x, end.x);
		Args.validateRange(p.y, start.y, end.y);
		Logf.info(log, "Finding path to %s for %s", p, unit);
		
		if (locations[p.x][p.y] == null || locations[p.x][p.y].getMinDistance() > unit.getMove()){
			return null;
		}
		
		ArrayDeque<LocationInfo> maybePath =  paths.get(p);
		if (maybePath != null) return maybePath;
		
		ArrayDeque<LocationInfo> path = new ArrayDeque<LocationInfo>();
		
		for(LocationInfo l = locations[p.x][p.y]; l != locations[unit.getGridX()][unit.getGridY()];l = l.getPrevious()){
			path.push(l);
			log.trace("path " + path);
		}
		
		path.push(locations[unit.getGridX()][unit.getGridY()]);
		paths.put(p,path);
		log.info("finalpath " + path);

		if (log.isDebugEnabled()){

			for (LocationInfo l : path) {
				System.out.printf("(%s,%s) ", l.x,l.y);
			}
			System.out.println();
			
			for (LocationInfo l : path) {
				System.out.print(l.getDirection()+ "     ");
			}
			System.out.println();

		}
		
		return Collections.unmodifiableCollection(path);
	}
	
	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return unit.getCost(map.getTile(x, y), map.getTile(newX, newY));
	}

}
