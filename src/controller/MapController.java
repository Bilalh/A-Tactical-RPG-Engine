package controller;

import common.Location;
import java.util.*;

import common.interfaces.IUnit;

import view.GuiUnit;
import engine.map.IModelUnit;
import engine.map.Map;
import engine.map.Tile;
import engine.map.Unit;

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

	public void moveUnit(UUID uuid, Location fieldLocation) {
		map.moveUnit(mapping.get(uuid),fieldLocation);
	}

	public void setUsersUnits(java.util.Map<UUID, Location> selectedPostions){
		
		ArrayList<IModelUnit> selected = new ArrayList<IModelUnit>();
		
		for(java.util.Map.Entry<UUID, Location> e: selectedPostions.entrySet()){
			IModelUnit u = mapping.get(e.getKey());
			u.setLocation(e.getValue());
			selected.add(u);
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
	
	public Collection<Location> getMovementRange(UUID uuid){
		IModelUnit u = mapping.get(uuid);
		return map.getMovementRange(u);
	}
	
}
