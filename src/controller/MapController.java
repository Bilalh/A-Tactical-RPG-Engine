package controller;

import common.Location;
import common.LocationInfo;

import java.util.*;

import org.apache.log4j.Logger;

import notifications.map.MapFinishedNotification;

import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import config.xml.TileImageData;

import util.Args;
import view.units.AnimatedUnit;
import view.units.GuiUnit;
import engine.map.Map;
import engine.map.Tile;
import engine.map.interfaces.IMutableMapUnit;
import engine.pathfinding.PathFinder;
import engine.unit.IMutableUnit;
import engine.unit.Unit;

/**
 * @author Bilal Hussain
 */
public class MapController extends Controller {
	private static final Logger log = Logger.getLogger(MapController.class);
	private Map map;

	public MapController(Map map) {
		this.map = map;
	}

	public void startMap() {
		map.start();
	}

	public void addMapObserver(Observer o) {
		map.addObserver(o);

	}

	public Tile[][] getField() {
		return map.getField();
	}

	public TileImageData getTileImageData(int x, int y) {
		return map.getTileImageData(x, y);
	}

	public String getTileSheetLocation() {
		return map.getTileSheetLocation();
	}

	public Collection<LocationInfo> getMovementRange(IMapUnit u) {
		Args.nullCheck(u);
		return map.getMovementRange((IMutableMapUnit) u);
	}

	public void setUsersUnits(HashMap<IUnit, Location> selectedPostions) {

		HashMap<IMutableUnit, Location> selected = new HashMap<IMutableUnit, Location>();
		for (java.util.Map.Entry<IUnit, Location> e : selectedPostions.entrySet()) {
			selected.put((IMutableUnit) e.getKey(), e.getValue());
		}

		map.setUsersUnits(selected);
	}

	public void moveUnit(IMapUnit u, Location fieldLocation) {
		map.moveUnit((IMutableMapUnit) u, fieldLocation);
	}

	public void finishedMoving(IMapUnit u) {
		map.finishedMoving((IMutableMapUnit) u);
	}

	public void unitTurnFinished(IMapUnit u) {
		map.unitTurnFinished((IMutableMapUnit) u);
	}

	public Collection<Location> getVaildTargets(IMapUnit u){
		return map.getVaildTargets((IMutableMapUnit) u);
	}

	public void targetChosen(IMapUnit u, IMapUnit target ){
		map.attackTargetChosen((IMutableMapUnit)u, (IMutableMapUnit)target);
	}


	public void skillTargetChosen(IMapUnit u, IMapUnit target) {
		map.skillTargetChosen((IMutableMapUnit) u, (IMutableMapUnit) target);
	}
	
	public void mapWon() {
		MapFinishedNotification n = new MapFinishedNotification();
		setChanged();
		log.info("mapWon");
		notifyObservers(n);
	}

	public void mapLost() {
		log.info("mapLost");
		// FIXME mapLost method
		mapWon();
	}
	
}
