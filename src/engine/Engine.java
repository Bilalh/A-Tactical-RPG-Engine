package engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import common.interfaces.IWeapon;

import view.Main;
import config.Config;
import config.assets.AssetStore;
import config.assets.AssetsLocations;
import config.assets.Units;
import config.assets.Weapons;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.Map;
import engine.map.interfaces.IMap;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * The engine is the backend of the game. It handles the progression of the maps. 
 * @author Bilal Hussain
 */
public class Engine {

	private Player player;
	private Map currentMap;
	
	public Engine(){
		loadSettings();
	}
	
	private void loadSettings() {
		
		AssetsLocations as = new AssetsLocations(
				"assets/weapons.xml", 
				"assets/skills.xml",
				"assets/music.xml",
				"assets/sounds.xml");
		AssetStore.instance().loadAssets(as);
		player = new Player();
		
		Units uu = Config.loadPreference("assets/units.xml");
		player.addUnits(uu.values());
	}

	public Map startMap(String name){
		currentMap = new Map(name, player);
		return currentMap;
	}

	public IMap getCurrentMap() {
		return currentMap;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void setPlayerUnits(Units uu){
		player.getUnits().clear();
		player.addUnits(uu.values());
	}
	
}
