package controller;

import java.awt.Point;
import java.util.*;

import common.interfaces.IUnit;

import view.GuiUnit;
import engine.Map;
import engine.Tile;
import engine.Unit;

/**
 * @author Bilal Hussain
 */
public class MapController extends Controller{

	private HashMap<UUID, Unit> mapping;
	private Map map;
	
	/** @category Generated */
	public MapController(Map map) {
		this.map = map;
		mapping = new HashMap<UUID, Unit>();
		for (Unit u : map.getUnits()){
			mapping.put(u.getUuid(), u);
		}
		
	}

	public void moveUnit(UUID uuid, int gridX, int gridY){
		map.moveUnit(mapping.get(uuid), gridX, gridY);
	}
	
	public void setUsersUnits(java.util.Map<UUID, Point> selectedPostions){
		
		ArrayList<Unit> selected = new ArrayList<Unit>();
		
		for(java.util.Map.Entry<UUID, Point> e: selectedPostions.entrySet()){
			Unit u = mapping.get(e.getKey());
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
}
