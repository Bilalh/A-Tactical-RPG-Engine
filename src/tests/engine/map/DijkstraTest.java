package tests.engine.map;

import java.awt.Point;
import java.util.Arrays;

import org.junit.Test;

import engine.map.*;
import static org.junit.Test.*;
import static org.junit.Assert.*;

/**
 * @author Bilal Hussain
 */
public class DijkstraTest {

	Dijkstra d;
	MockMovementCostProvider provider = new MockMovementCostProvider();
	Location[][] exp; 
	
	void setupSimple() {
		int[][] temp = {
				{ 3, 4, 3, 3, 1 },
				{ 1, 8, 1, 3, 2 },
				{ 1, 3, 3, 12, 9 },
				{ 1, 1, 5, 1, 1 },
				{ 5, 1, 1, 1, 9 }
		};		
		provider.setCosts(temp);
		d = new Dijkstra(provider, temp.length, temp[0].length);
		exp= new Location[temp.length][temp[0].length];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp.length; j++) {
				exp[i][j] = new Location(i,j, Integer.MAX_VALUE,null);
			}
		}
	}

	@Test
	public void testSimple() {
		setupSimple();
		Location[][] arr =  d.calculate(new Point(0, 0));
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
		
		// expected values
		l(0,0,  0);       l(0,1,  2,  0,0); l(0,2,  4,  0,1); l(0,3,  5,  0,2); l(0,4,  8,  0,3); 
		l(1,0,  3,  0,0); l(1,1,  7,  0,1); l(1,2,  7,  0,2); l(1,3,  6,  0,3); l(1,4,  8,  1,3); 
		l(2,0,  4,  1,0); l(2,1,  7,  2,0); l(2,2,  8,  2,1); l(2,3, 16,  1,3); l(2,4, 16,  1,4); 
		l(3,0,  5,  2,0); l(3,1,  6,  3,0); l(3,2, 11,  3,1); l(3,3, 10,  4,3); l(3,4, 11,  3,3); 
		l(4,0, 10,  3,0); l(4,1,  7,  3,1); l(4,2,  8,  4,1); l(4,3,  9,  4,2); l(4,4, 18,  4,3); 
		
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
	
}
