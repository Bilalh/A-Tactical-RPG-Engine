package engine;

import engine.PathfindingEx.TileBasedMap;
import engine.map.IMap;
import engine.map.Map;

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
		
		Unit u        = new Unit();
		UnitImages ui = new UnitImages(); 
		u.setName("Elena");
		u.setMove(3);
		u.setSpeed(20);
		u.setStrength(30);
		u.setDefence(20);
		player.addUnit(u);
		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		
		u  = new Unit();
		ui = new UnitImages();
		u.setName("Boy");
		u.setMove(4);
		u.setSpeed(60);
		u.setStrength(10);
		u.setDefence(10);
		player.addUnit(u);
		ui.setSpriteSheetLocation("images/characters/Boy.png");
		u.setImageData(ui);
		
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
