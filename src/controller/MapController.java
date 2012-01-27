package controller;

import common.Location;
import java.util.*;

import common.interfaces.IUnit;
import config.xml.TileImageData;

import view.GuiUnit;
import engine.map.IModelUnit;
import engine.map.Map;
import engine.map.Tile;
import engine.map.Unit;
import engine.pathfinding.LocationInfo;
import engine.pathfinding.PathFinder;

/**
 * @author Bilal Hussain
 */
public class MapController extends Controller {

	private HashMap<UUID, IModelUnit> mapping;
	private Map map;

	/** @category Generated */
	public MapController(Map map) {
		this.map = map;
		mapping = new HashMap<UUID, IModelUnit>();
		for (IModelUnit u : map.getUnits()) {
			mapping.put(u.getUuid(), u);
		}

	}

	public void moveUnit(UUID uuid, Location fieldLocation) {
		map.moveUnit(mapping.get(uuid), fieldLocation);
	}

	public void setUsersUnits(java.util.Map<UUID, Location> selectedPostions) {

		ArrayList<IModelUnit> selected = new ArrayList<IModelUnit>();

		for (java.util.Map.Entry<UUID, Location> e : selectedPostions.entrySet()) {
			IModelUnit u = mapping.get(e.getKey());
			u.setLocation(e.getValue());
			selected.add(u);
		}

		map.setUsersUnits(selected);
	}

	public void startMap() {
		map.start();
	}

	public void addMapObserver(Observer o) {
		map.addObserver(o);
	}

	public ArrayList<LocationInfo> getMovementRange(UUID u) {
		return map.getMovementRange(mapping.get(u));
	}

	public Tile[][] getGrid() {
		return map.getField();
	}

	public TileImageData getTileImageData(int x, int y){
		return map.getTileImageData(x, y);
	}
	
}
