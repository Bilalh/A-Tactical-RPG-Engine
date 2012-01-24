package tests.engine.pathfinding;

import java.util.ArrayList;

import common.Location;

import engine.map.IMap;
import engine.map.IModelUnit;
import engine.map.Tile;
import engine.map.Unit;

/**
 * @author Bilal Hussain
 */
public class MapStub implements IMap {

	@Override
	public void start() {
		// TODO start method
		
	}

	@Override
	public void moveUnit(IModelUnit u, Location p) {
		// TODO moveUnit method
		
	}

	@Override
	public void setUsersUnits(ArrayList<IModelUnit> selected) {
		// TODO setUsersUnits method
		
	}

	@Override
	public Tile getTile(int x, int y) {
		// TODO getTile method
		return null;
	}

	@Override
	public int getFieldHeight() {
		// TODO getFieldHeight method
		return 0;
	}

	@Override
	public int getFieldWidth() {
		// TODO getFieldWidth method
		return 0;
	}

	@Override
	public boolean isPlayersTurn() {
		// TODO isPlayersTurn method
		return false;
	}

	@Override
	public ArrayList<Unit> getUnits() {
		// TODO getUnits method
		return null;
	}

}
