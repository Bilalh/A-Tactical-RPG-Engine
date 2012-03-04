package engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import common.interfaces.IWeapon;

import view.Main;
import config.Config;
import engine.assets.AssertStore;
import engine.assets.AssetsLocations;
import engine.assets.Units;
import engine.assets.Weapons;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.Map;
import engine.map.interfaces.IMap;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

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
		
		AssetsLocations as = new AssetsLocations(
				"assets/weapons.xml", 
				"assets/skills.xml");
		AssertStore.instance().loadAssets(as);
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
	
}
