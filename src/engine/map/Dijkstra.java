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

	static class Location implements Comparable<Location>{
		int x;
		int y;
		int minDistance;
		Location previous;
		
		/** @category Generated Constructor */
		public Location(int x, int y, int minDistance) {
			this.x = x;
			this.y = y;
			this.minDistance = minDistance;
		}

		@Override
		public int compareTo(Location o) {
			return this.minDistance  < o.minDistance ? -1 
					: (this.minDistance == o.minDistance ? 0 
					: 1);  
		}

		@Override
		public String toString() {
			return previous != null ?
					String.format("{%s,%s} %s <%s,%s>]",
							x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance, previous.x, previous.y)
					: String.format("{%s,%s} %s, null]",
							x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance);
		}
	}

	// Stores infomation about locations
	private Location[][] locations;
	
	// The starting point.
	private Point start;
	
	private int rows, cols;
	
	/** @category Generated Constructor */
	public Dijkstra(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		locations = new Location[rows][cols];

	}

	// To get 
	static final int[][] dirs = {
		{0,1},   // up 
		{0,-1},  // down
		{-1,0},  // left
		{1,0},   // right
	};		
	
	public void calculate(Point start){
		int upperX = locations.length,    lowerX =0;
		int upperY = locations[0].length, lowerY=0;
		
		PriorityQueue<Location> pq = new PriorityQueue<Dijkstra.Location>();
		
		for (int i = 0; i < locations.length; i++) {
			for (int j = 0; j < locations[0].length; j++) {
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
				newCost += getCost(u.x, u.y, nx, ny);
				
				System.out.printf("    {%s,%s}\n\tnewcost:%s\n", nx,ny ,newCost);
				System.out.println("\tv:"  + v);
				
				if (newCost < v.minDistance){
					v.minDistance = (int) newCost; // safe since less then Integer.MAX_VALUE
					v.previous =u;
					System.out.println("     Updated");
				}
			}
			
		}
		
	}
	
	
	int[][] temp = {
			{3, 4, 3, 3, 1},
			{1, 1, 1, 3, 2},
			{3, 1, 3, 2, 2},
			{1, 1, 3, 3, 5},
			{4, 1, 1, 1, 3}
	};
	int getCost(int x, int y,  int newX, int newY){
		return   1 +  Math.abs(temp[x][y] - temp[newX][newY]);
	}
	
	public static void main(String[] args) {
		Dijkstra d = new Dijkstra(5, 5);
		d.calculate(new Point(0, 0));
		
		for (int i = 0; i < d.locations.length; i++) {
			System.out.println(Arrays.toString(d.temp[i]));
		}
		
		for (int i = 0; i < d.locations.length; i++) {
			System.out.println(Arrays.toString(d.locations[i]));
		}
		
	}

}