package controller;

import common.Location;
import common.LocationInfo;

import java.util.*;

import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import config.xml.TileImageData;

import view.GuiUnit;
import engine.IMutableUnit;
import engine.Unit;
import engine.map.IMutableMapUnit;
import engine.map.Map;
import engine.map.Tile;
import engine.pathfinding.PathFinder;

/**
 * @author Bilal Hussain
 */
public class MapController extends Controller {

	private Map map;

	/** @category Generated */
	public MapController(Map map) {
		this.map = map;
	}

	public void moveUnit(IMapUnit u , Location fieldLocation) {
		map.moveUnit((IMutableMapUnit) u, fieldLocation);
	}

	public void setUsersUnits(HashMap<IUnit, Location> selectedPostions) {

		HashMap<IMutableUnit, Location> selected =  new HashMap<IMutableUnit, Location>();
		for (java.util.Map.Entry<IUnit, Location> e : selectedPostions.entrySet()) {
			selected.put((IMutableUnit) e.getKey(), e.getValue());
		}

		map.setUsersUnits(selected);
	}

	public void startMap() {
		map.start();
	}

	public void addMapObserver(Observer o) {
		map.addObserver(o);
	}

	public Collection<LocationInfo> getMovementRange(IMapUnit u) {
		return map.getMovementRange((IMutableMapUnit) u);
	}

	public Tile[][] getGrid() {
		return map.getField();
	}

	public TileImageData getTileImageData(int x, int y){
		return map.getTileImageData(x, y);
	}
	
}
