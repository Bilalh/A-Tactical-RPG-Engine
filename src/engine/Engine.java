package engine;

import config.Config;
import engine.ai.Map;
import engine.map.interfaces.IMap;
import engine.unit.Unit;
import engine.unit.UnitImages;

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
		UnitImages ui = Config.loadPreference("images/characters/princess-animations.xml");
		u.setName("Elena");
		u.setMove(3);
		u.setSpeed(20);
		u.setStrength(30);
		u.setDefence(20);
		u.setMaxHp(20);
		player.addUnit(u);
//		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		
		u  = new Unit();
		ui = Config.loadPreference("images/characters/Boy-animations.xml");
		u.setName("Boy");
		u.setMove(4);
		u.setSpeed(60);
		u.setStrength(30);
		u.setDefence(10);
		u.setMaxHp(30);
		player.addUnit(u);
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
