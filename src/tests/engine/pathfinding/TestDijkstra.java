package tests.engine.pathfinding;

import common.Location;
import java.util.Arrays;

import org.junit.Test;

import engine.pathfinding.*;
import static org.junit.Test.*;
import static org.junit.Assert.*;

/**
 * @author Bilal Hussain
 */
public class TestDijkstra extends Tests {

	Dijkstra d;
	MockMovementCostProvider provider = new MockMovementCostProvider();
	LocationInfo[][] exp; 
	
	void setupSimple() {
		int[][] cost = {
				{ 3, 4, 3, 3, 1 },
				{ 1, 8, 1, 3, 2 },
				{ 1, 3, 3, 12, 9 },
				{ 1, 1, 5, 1, 1 },
				{ 5, 1, 1, 1, 9 }
		};		
		provider.setCosts(cost);
		d = new Dijkstra(provider, cost.length, cost[0].length);
		exp= new LocationInfo[cost.length][cost[0].length];
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost[0].length; j++) {
				exp[i][j] = new LocationInfo(i,j, Integer.MAX_VALUE,null);
			}
		}
	}

	// Tests graphs where rows ≠ columns.	
	void setupUnequal(){
		int[][] costs ={
				{5, 4, 5, 0, 3, 2},
				{6, 5, 8, 4, 5, 4},
				{6, 0, 7, 7, 0, 4}};
		provider.setCosts(costs);
		d = new Dijkstra(provider, costs.length, costs[0].length);
		exp= new LocationInfo[costs.length][costs[0].length];
		for (int i = 0; i < costs.length; i++) {
			for (int j = 0; j < costs[0].length; j++) {
				exp[i][j] = new LocationInfo(i,j, Integer.MAX_VALUE,null);
			}
		}
	}
	
	@Test
	public void testSimple() {
		setupSimple();
		LocationInfo[][] arr =  d.calculate(new Location(0, 0));
//		for (int i = 0; i < arr.length; i++) {
//			for (int j = 0; j < arr[i].length; j++) {
//				if (arr[i][j].getPrevious() == null){
//					System.out.printf("l(%s,%s, %2s); ", i,j, arr[i][j].getMinDistance());
//				}else{
//					System.out.printf("l(%s,%s, %2s,  %s,%s); ", i,j, arr[i][j].getMinDistance(),
//							arr[i][j].getPrevious().getX() ,arr[i][j].getPrevious().getY() );	
//				}
//			}
//			System.out.println();
//		}
//		
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println(Arrays.toString(arr[i]));
//		}
//		
		// expected values
		l(0,0,  0);       l(0,1,  2,  0,0); l(0,2,  4,  0,1); l(0,3,  5,  0,2); l(0,4,  8,  0,3); 
		l(1,0,  3,  0,0); l(1,1,  7,  0,1); l(1,2,  7,  0,2); l(1,3,  6,  0,3); l(1,4,  8,  1,3); 
		l(2,0,  4,  1,0); l(2,1,  7,  2,0); l(2,2,  8,  2,1); l(2,3, 16,  1,3); l(2,4, 16,  1,4); 
		l(3,0,  5,  2,0); l(3,1,  6,  3,0); l(3,2, 11,  3,1); l(3,3, 10,  4,3); l(3,4, 11,  3,3); 
		l(4,0, 10,  3,0); l(4,1,  7,  3,1); l(4,2,  8,  4,1); l(4,3,  9,  4,2); l(4,4, 18,  4,3); 
		
		assertArrayEquals("Results", exp, arr);
	}
	
	
	
	@Test 
	public void testWithRange(){
		setupSimple();
		LocationInfo[][] arr =  d.calculate(new Location(0,0), 0, 3, 0, 3);
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println(Arrays.toString(arr[i]));
//		}
		
		// expected values
		l(0,0,  0);       l(0,1,  2,  0,0); l(0,2,  4,  0,1); l(0,3); l(0,4); 
		l(1,0,  3,  0,0); l(1,1,  7,  0,1); l(1,2,  7,  0,2); l(1,3); l(1,4); 
		l(2,0,  4,  1,0); l(2,1,  7,  2,0); l(2,2,  8,  2,1); l(2,3); l(2,4); 
		
		l(3,0); l(3,1); l(3,2); l(3,3); l(3,4); 
		l(4,0); l(4,1); l(4,2); l(4,3); l(4,4); 
		
		assertArrayEquals("Results", exp, arr);
	}
	
	// Tests a graph where rows ≠ columns.
	// Tests have 0 cost.
	// Tests multiple shortest paths
	@Test
	public void testWitUnequal(){
		setupUnequal();
		LocationInfo[][] arr = d.calculate(new Location(2,2));
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println(Arrays.toString(arr[i]));
//		}		
		
		l(0,0, 10,  0,1); l(0,1,  8,  0,2); l(0,2,  6,  1,2); l(0,3, 10,  1,3); l(0,4, 10,  1,4); l(0,5, 12,  1,5); 
		l(1,0,  8,  1,1); l(1,1,  6,  1,2); l(1,2,  2,  2,2); l(1,3,  5,  2,3); l(1,4,  7,  1,3); l(1,5,  9,  1,4); 
		l(2,0,  9,  1,0); l(2,1,  8,  2,2); l(2,2,  0);       l(2,3,  1,  2,2); l(2,4,  9,  2,3); l(2,5, 10,  1,5); 
		
		assertArrayEquals("Results", exp, arr);
	}
	
	
	// To easily set the expected values
	
	private void l(int x, int y, int dist, int lx, int ly){
		exp[x][y].setMinDistance(dist);
		exp[x][y].setPrevious(exp[x][y]);
	}
	
	private void l(int x, int y, int dist){
		exp[x][y].setMinDistance(dist);
	}
	
	private void l(int x,int y){
		exp[x][y] = null;
	}
	
}
