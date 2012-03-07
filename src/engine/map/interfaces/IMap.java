package engine.map.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import common.Location;
import common.LocationInfo;
import common.interfaces.ILocation;
import common.interfaces.IMapUnit;

import config.xml.TileImageData;
import engine.map.Tile;
import engine.unit.IMutableUnit;

/**
 * @author bilalh
 */
public interface IMap {

	void start();

	void setUsersUnits(HashMap<IMutableUnit, Location> selected);

	// Precondition getMovementRange must have been called first
	void moveUnit(IMutableMapUnit u, ILocation p);

	Collection<LocationInfo> getMovementRange(IMutableMapUnit u);

	Tile getTile(int x, int y);

	TileImageData getTileImageData(int x, int y);

	int getFieldWidth();

	int getFieldHeight();

	void dialogFinished();
	
}