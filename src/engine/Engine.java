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
		player.addUnit(new Unit("a", 33, 4, 4));
		player.addUnit(new Unit("b", 66, 3, 2));
	}


	public Map startMap(String name){
		currentMap = new Map(name, player);
		return currentMap;
	}

	/** @category Generated Getter */
	public Map getCurrentMap() {
		return currentMap;
	}
	
}
