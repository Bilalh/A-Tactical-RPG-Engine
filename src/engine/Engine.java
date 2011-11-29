package engine;

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
	public Map getCurrentMap() {
		return currentMap;
	}
	
}
