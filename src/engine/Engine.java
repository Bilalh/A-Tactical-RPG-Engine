package engine;

import engine.PathfindingEx.TileBasedMap;
import engine.map.IMap;
import engine.map.Map;
import engine.map.Player;
import engine.map.Unit;

/**
 * @author bilalh
 */
public class Engine {

	private Player player;
	private Map currentMap;
	
	public Engine(){
		loadSettings();
	}
	
	private void loadSettings() {
		player = new Player();
		player.addUnit(new Unit("unitA", 33, 4, 50));
		player.addUnit(new Unit("unitB", 66, 3, 30));
	}


	public Map startMap(String name){
		currentMap = new Map(name, player);
		return currentMap;
	}

	/** @category Generated */
	public IMap getCurrentMap() {
		return currentMap;
	}
	
}
