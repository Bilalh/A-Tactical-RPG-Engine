package engine.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import common.Location;
import common.LocationInfo;
import common.interfaces.IMapUnit;

import config.xml.TileImageData;
import engine.IMutableUnit;

/**
 * @author bilalh
 */
public interface IMap {

	void start();

	void setUsersUnits(HashMap<IMutableUnit, Location> selected);

	// Precondition getMovementRange must have been called first
	void moveUnit(IMutableMapUnit u, Location p);

	Collection<LocationInfo> getMovementRange(IMutableMapUnit u);

	ArrayList<IMapUnit> getUnits();

	Tile getTile(int x, int y);

	TileImageData getTileImageData(int x, int y);

	/** @category Generated */
	boolean isPlayersTurn();

	/** @category Generated Getter */
	int getFieldWidth();

	/** @category Generated Getter */
	int getFieldHeight();

}