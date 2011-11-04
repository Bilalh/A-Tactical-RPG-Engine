package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;
import java.util.UUID;

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
	
	
	/** @category Generated Constructor */
	public MapController(Map map) {
		this.map = map;
		mapping = new HashMap<UUID, Unit>();
	}

	public void moveUnit(UUID uuid, int gridX, int gridY){
		map.moveUnit(mapping.get(uuid), gridX, gridY);
	}
	
	public void setUsersUnits(ArrayList<Unit> units){
		for (Unit u : units) {
			mapping.put(u.getUuid(), u);
		}
		map.setUsersUnits(units);
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
