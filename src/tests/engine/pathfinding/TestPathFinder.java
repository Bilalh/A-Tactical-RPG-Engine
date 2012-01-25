package tests.engine.pathfinding;

import common.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import config.Args;

import engine.map.IMap;
import engine.map.IModelUnit;
import engine.map.Unit;
import engine.pathfinding.*;
import static org.junit.Test.*;
import static org.junit.Assert.*;

/**
 * @author Bilal Hussain
 */
public class TestPathFinder extends Tests {
	
	PathFinder pf;
	MockMap map;
	IModelUnit unit;
	ArrayList<LocationInfo> exp;
	
	@Before
	public void setup(){
		exp = new ArrayList<LocationInfo>();
		map = new MockMap();
		unit = new Unit("dd", 31, 4, 41);
		unit.setLocation(new Location(0,0));
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

	@Test
	public void testSimpleRange() {
		setupSimple();
		unit.setMove(4);
		pf = new PathFinder(unit, map);
		ArrayList<LocationInfo> actual =  pf.getMovementRange();
		l(0,0);
		l(0,1);
		l(0,2);
		l(1,0);
		l(2,0);
		compare(exp,actual);
	}
	
	@Test
	public void testPath(){
		setupSimple();
		unit.setMove(31);
		unit.setLocation(new Location(2,2));
		pf = new PathFinder(unit, map);
		ArrayList<LocationInfo> actual = pf.getMovementPath(new Location(4, 3));
		
		l(2,2);
		l(3,2);
		l(4,2);
		l(4,3);
		compare(exp, actual);
	}
	
	private void compare(Collection<LocationInfo> exp, Collection<LocationInfo> act){
		Iterator<LocationInfo> lexp  = exp.iterator();
		Iterator<LocationInfo> lact  = act.iterator();
		
		assertEquals("size\n" + exp + "\n" + act +"\n" , exp.size(),act.size());
		
		while(lexp.hasNext() && lact.hasNext()){
			LocationInfo e = lexp.next();
			LocationInfo a = lact.next();
			assertEquals("xs" + e + " "  + a , e.getX(), a.getX());
			assertEquals("ys" + e + " "  + a, e.getY(), a.getY());
		}
		
		
	}
	
	private void l(int x, int y){
		exp.add(new LocationInfo(x, y,-1));
	}
	
}