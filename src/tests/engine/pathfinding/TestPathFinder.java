package tests.engine.pathfinding;

import common.Location;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Test;

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
	MockMap map = new MockMap();
	IModelUnit unit = new Unit("dd", 3, 3, 4);

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
	public void testSimplePath() {
		setupSimple();
		pf = new PathFinder(unit, map);

	}

}
