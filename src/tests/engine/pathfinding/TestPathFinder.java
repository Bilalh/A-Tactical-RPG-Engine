package tests.engine.pathfinding;

import common.Location;
import common.LocationInfo;

import java.util.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import util.*;


import engine.Unit;
import engine.map.IMap;
import engine.map.IMutableMapUnit;
import engine.map.MapUnit;
import engine.pathfinding.*;
import static org.junit.Test.*;
import static org.junit.Assert.*;

/**
 * @author Bilal Hussain
 */
public class TestPathFinder extends Tests {
	
	PathFinder pf;
	MockMap map;
	IMutableMapUnit unit;
	ArrayList<LocationInfo> exp;
	
	@Before
	public void setup(){
		exp = new ArrayList<LocationInfo>();
		map = new MockMap();
		unit = new MapUnit(new Unit("d", 2, 3, 4), new Location(0, 0));
	}
	
	void setupSimple() {
		int[][] costs = {
				{ 3, 4, 3, 3, 1 },
				{ 1, 8, 1, 3, 2 },
				{ 1, 3, 3, 12, 9 },
				{ 1, 1, 5, 1, 1 },
				{ 5, 1, 1, 1, 9 }
		};
		map.setTiles(costs);
	}

	Comparator<LocationInfo> cc = new Comparator<LocationInfo>() {
		
		@Override
		public int compare(LocationInfo o1, LocationInfo o2) {
			if     (o1.getX() < o2.getX()) return -1;
			else if(o1.getX() > o2.getX()) return 1;
			
			if     (o1.getY() < o2.getY()) return -1;
			else if(o1.getY() > o2.getY()) return 1;
			return 0;
		}
	};
	
	@Test
	public void testSimpleRange() {
		setupSimple();
		unit.setMove(4);
		pf = new PathFinder(unit, map);
		Collection<LocationInfo> actual =  pf.getMovementRange();
		l(0,0);
		l(0,1);
		l(0,2);
		l(1,0);
		l(2,0);

		System.out.println("a" + actual);
		System.out.println("e" +exp);
		
		ArrayList<LocationInfo> aa = new ArrayList<LocationInfo>(actual);
		Collections.sort(aa, cc);
		Collections.sort(exp, cc);
		compare(exp, aa);
	}
	
	@Test
	public void testPath(){
		setupSimple();
		unit.setMove(31);
		unit.setLocation(new Location(2,2));
		pf = new PathFinder(unit, map);
		Collection<LocationInfo> actual = pf.getMovementPath(new Location(4, 3));
		
		l(2,2);
		l(2,1);
		l(3,1);
		l(4,1);
		l(4,2);
		l(4,3);
		compare(exp, actual);
	}

	// Tests multiple answers 
	@Test
	public void testPath2(){
		int[][] costs = new int[7][7];
		for (int i = 0; i < costs.length; i++) {
			for (int j = 0; j < costs.length; j++) {
				costs[i][j] =1;
			}
		}
		map.setTiles(costs);
		
		unit.setMove(3);
		unit.setLocation(new Location(2,5));
		pf = new PathFinder(unit, map);
		Collection<LocationInfo> actual = pf.getMovementPath(new Location(3, 3));

		l(2,5);
		l(2,4);
		l(2,3);
		l(3,3);
		int actualSum = 0;
		int expSum =0;
		
		for (LocationInfo l : actual) {
			actualSum += costs[l.x][l.y];
		}

		for (LocationInfo l : exp) {
			expSum += costs[l.x][l.y];
		}
		
		assertEquals("cost", expSum,actualSum);
	}
	
	private void compare(Collection<LocationInfo> exp, Collection<LocationInfo> act){
		Iterator<LocationInfo> lexp  = exp.iterator();
		Iterator<LocationInfo> lact  = act.iterator();
		
		assertEquals("size\n" + exp + "\n" + act +"\n" , exp.size(),act.size());
		
		while(lexp.hasNext() && lact.hasNext()){
			LocationInfo e = lexp.next();
			LocationInfo a = lact.next();
			assertEquals("xs " + e + " "  + a , e.getX(), a.getX());
			assertEquals("ys " + e + " "  + a, e.getY(), a.getY());
		}
		
		
	}
	
	private void l(int x, int y){
		exp.add(new LocationInfo(x, y,-1));
	}
	
}