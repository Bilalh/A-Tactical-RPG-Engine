package controller;

import java.awt.Point;
import java.util.*;

import common.interfaces.IUnit;

import view.GuiUnit;
import engine.Map;
import engine.Tile;
import engine.Unit;
import engine.interfaces.IModelUnit;

/**
 * @author Bilal Hussain
 */
public class MapController extends Controller{

	private HashMap<UUID, IModelUnit> mapping;
	private Map map;
	
	/** @category Generated */
	public MapController(Map map) {
		this.map = map;
		mapping = new HashMap<UUID, IModelUnit>();
		for (IModelUnit u : map.getUnits()){
			mapping.put(u.getUuid(), u);
		}
		
	}

	public void moveUnit(UUID uuid, Point fieldLocation) {
		map.moveUnit(mapping.get(uuid),fieldLocation);
	}

	public void setUsersUnits(java.util.Map<UUID, Point> selectedPostions){
		
		ArrayList<Unit> selected = new ArrayList<Unit>();
		
		for(java.util.Map.Entry<UUID, Point> e: selectedPostions.entrySet()){
			IModelUnit u = mapping.get(e.getKey());
			u.setLocation(e.getValue());
		}
		
		map.setUsersUnits(selected);
	}
	
	public Tile[][] getGrid(){
		return map.getField();
	}

	public void startMap() {
		map.start();
	}

	public void addMapObserver(Observer o){
		map.addObserver(o);
	}
	
	public Collection<Point> getMovementRange(UUID uuid){
		IModelUnit u = mapping.get(uuid);
		return map.getMovementRange(u);
	}
	
}
