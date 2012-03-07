package tests.engine.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
import config.xml.TileImageData;

import engine.map.Tile;
import engine.map.interfaces.IMap;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.IMutableUnit;
import engine.unit.Unit;

/**
 * @author Bilal Hussain
 */
public class MapStub implements IMap {

	@Override
	public void start() {

	}

	@Override
	public void setUsersUnits(HashMap<IMutableUnit, Location> selected) {

	}


	@Override
	public void moveUnit(IMutableMapUnit u, ILocation p) {

	}

	@Override
	public Collection<LocationInfo> getMovementRange(IMutableMapUnit u) {
		return null;
	}

	@Override
	public Tile getTile(int x, int y) {
		return null;
	}

	@Override
	public TileImageData getTileImageData(int x, int y) {
		return null;
	}

	@Override
	public int getFieldWidth() {
		return 0;
	}

	@Override
	public int getFieldHeight() {
		return 0;
	}

	@Override
	public void dialogFinished() {
		// FIXME dialogFinished method
		
	}

}
