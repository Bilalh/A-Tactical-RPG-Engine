package engine.pathfinding;

import common.Direction;
import common.Location;
import java.util.*;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;

import config.Logf;

/**
 * @author Bilal Hussain
 */
public class Dijkstra {

	private static final Logger log = Logger.getLogger(Dijkstra.class);
	
	// Map size 
	private int rows, cols;
	// Gives the cost between two points. 
	private IMovementCostProvider costProvider;

	public Dijkstra(IMovementCostProvider costProvider, int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		this.costProvider = costProvider;
	}
	
	// To get the neighbours. 
	private static final int[][] dirs = {
			{ 0, 1 },  // up
			{ 0, -1 }, // down
			{ -1, 0 }, // left
			{ 1, 0 },  // right
	};

	// Retuens the min cost to all connected nodes	
	public LocationInfo[][] calculate(Location start) {
		return calculate(start, 0, rows, 0, cols);
	}
	
	public LocationInfo[][] calculate(Location start, int lowerX, int upperX, int lowerY, int upperY) {
		if (upperX <= lowerX || upperY <= lowerY || start == null ) throw new IllegalArgumentException("");
		
		System.out.println(start);
		
		LocationInfo[][] locations = new LocationInfo[rows][cols];
		HashSet<LocationInfo> settler = new HashSet<LocationInfo>();
		
		PriorityQueue<LocationInfo> pq = new PriorityQueue<LocationInfo>(upperX - lowerX * upperY - lowerY,
			new Comparator<LocationInfo>() {
				@Override
				public int compare(LocationInfo o1, LocationInfo o2) {
					return o1.getMinDistance() < o2.getMinDistance() ? -1
							: (o1.getMinDistance() == o2.getMinDistance() ? 0
							: 1);
				}
			});

		for (int i = lowerX; i < upperX; i++) {
			for (int j = lowerY; j < upperY; j++) {
				locations[i][j] = new LocationInfo(i, j, Integer.MAX_VALUE);
				pq.add(locations[i][j]);
			}
		}

		locations[start.x][start.y].setMinDistance(0);

		while (!pq.isEmpty()) {
			LocationInfo u = pq.poll();
			log.trace("Processing " + u);
			
			settler.add(u);
			
			for(Direction d : Direction.values()){
				if (d == Direction.STILL) continue;
				
				int nx = d.x + u.x;
				if (nx < lowerX || nx >= upperX) continue;
				int ny = d.y + u.y;
				if (ny < lowerY || ny >= upperY) continue;

				LocationInfo v = locations[nx][ny];
				LogMF.trace(log,"    <{0},{1}>", nx, ny);
				if (settler.contains(v) && v.getPrevious() != null){
					LogMF.trace(log,"      Skipped", nx,ny);
					continue;
				}
				
				long newCost = u.getMinDistance(); // To stop overflow (e.g Integer.MAX_VALUE + 10)
				newCost += costProvider.getMovementCost(u.x, u.y, nx, ny);

				LogMF.trace(log,"\tnewcost:{0}",newCost);
				log.trace("\tv:" + v);
				
				if (newCost < v.getMinDistance()) {
					v.setMinDistance((int) newCost); // safe since less then Integer.MAX_VALUE
					v.setPrevious(u);
					u.setNextDirection(d);
					pq.add(v);
					log.trace("      Updated");
				}
			}

		}
		return locations;
	}

}