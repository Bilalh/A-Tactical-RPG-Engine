package engine.pathfinding;

import java.awt.Point;
import java.util.*;

/**
 * @author Bilal Hussain
 */
public class Dijkstra {

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
	public Location[][] calculate(Point start) {
		return calculate(start, 0, rows, 0, cols);
	}
	
	public Location[][] calculate(Point start, int lowerX, int upperX, int lowerY, int upperY) {
		Location[][] locations = new Location[rows][cols];
		HashSet<Location> settler = new HashSet<Location>();
		
		PriorityQueue<Location> pq = new PriorityQueue<Location>(upperX - lowerX * upperY - lowerY,
			new Comparator<Location>() {
				@Override
				public int compare(Location o1, Location o2) {
					return o1.minDistance < o2.minDistance ? -1
							: (o1.minDistance == o2.minDistance ? 0
							: 1);
				}
			});

		for (int i = lowerX; i < upperX; i++) {
			for (int j = lowerY; j < upperY; j++) {
				locations[i][j] = new Location(i, j, Integer.MAX_VALUE);
				pq.add(locations[i][j]);
			}
		}

		locations[start.x][start.y].minDistance = 0;

		while (!pq.isEmpty()) {
			Location u = pq.poll();
			System.out.println("Processing " + u);
			
			settler.add(u);
			
			for (int[] pp : dirs) {
				int nx = pp[0] + u.x;
				if (nx < lowerX || nx >= upperX) continue;
				int ny = pp[1] + u.y;
				if (ny < lowerY || ny >= upperY) continue;

				Location v = locations[nx][ny];
				System.out.printf("    {%s,%s}\n", nx, ny);
				if (settler.contains(v) && v.previous != null){
					System.out.printf("      Skipped\n", nx,ny);
					continue;
				}
				
				long newCost = u.minDistance; // To stop overflow (e.g Integer.MAX_VALUE + 10)
				newCost += costProvider.getMovementCost(u.x, u.y, nx, ny);

				System.out.printf("\tnewcost:%s\n",newCost);
				System.out.println("\tv:" + v);

				if (newCost < v.minDistance) {
					v.minDistance = (int) newCost; // safe since less then Integer.MAX_VALUE
					v.previous = u;
					System.out.println("      Updated");
					pq.add(v);
				}
			}

		}
		return locations;
	}

}