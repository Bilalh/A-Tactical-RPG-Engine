package engine.map;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * @author Bilal Hussain
 */
public class Dijkstra {

	private int rows, cols;
	private IMovementCostProvider costProvider;
	
	public Dijkstra(IMovementCostProvider costProvider, int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		this.costProvider = costProvider;
	}

	// To get 
	static final int[][] dirs = {
		{0,1},   // up 
		{0,-1},  // down
		{-1,0},  // left
		{1,0},   // right
	};		
	
	public Location[][] calculate(Point start){
		Location[][] locations  = new Location[rows][cols];
		
		int upperX = locations.length,    lowerX =0;
		int upperY = locations[0].length, lowerY=0;
		
		PriorityQueue<Location> pq = new PriorityQueue<Location>();
		
		for (int i = lowerX; i < upperX; i++) {
			for (int j = lowerY; j < upperY; j++) {
				locations[i][j] = new Location(i, j, Integer.MAX_VALUE);
				pq.add(locations[i][j]);
			}
		}
		
		locations[start.x][start.y].minDistance = 0;
		
		while(!pq.isEmpty()){
			Location u = pq.poll();
			System.out.println("Processing " + u);
			for (int[] pp : dirs) {
				int nx = pp[0] + u.x;
				if (nx < lowerX || nx >= upperX ) continue;
				int ny = pp[1] + u.y;
				if (ny < lowerY || ny >= upperY ) continue;
				
				Location v   =  locations[nx][ny];
				long newCost = u.minDistance; // To stop overflow (e.g Integer.MAX_VALUE + 10)
				newCost += costProvider.getMovementCost(u.x, u.y, nx, ny);
				
				System.out.printf("    {%s,%s}\n\tnewcost:%s\n", nx,ny ,newCost);
				System.out.println("\tv:"  + v);
				
				if (newCost < v.minDistance){
					v.minDistance = (int) newCost; // safe since less then Integer.MAX_VALUE
					v.previous =u;
					System.out.println("     Updated");
					pq.add(v);
				}
			}
			
		}
		return locations;
	}

}