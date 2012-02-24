package engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import common.interfaces.IWeapon;

import view.Main;
import config.Config;
import engine.asserts.AssertStore;
import engine.asserts.Weapons;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.Map;
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
		
		Weapons ws =  Config.loadPreference("assets/weapons.xml");
		AssertStore.instance().loadWeapons(ws);
		
		player = new Player();
		Unit u        = new Unit();
		UnitImages ui = Config.loadPreference("images/characters/princess-animations.xml");
		u.setName("Elena");
		u.setMove(3);
		u.setSpeed(20);
		u.setStrength(25);
		u.setDefence(20);
		u.setMaxHp(15);
//		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData("images/characters/princess-animations.xml",ui);
//		u.setWeapon(new RangedWeapon(6, 5,3));
		u.setWeapon(AssertStore.instance().getWeapon(UUID.fromString("55ff186f-7a30-41d7-aefb-035b0c882260")));
		player.addUnit(u);

		u  = new Unit();
		ui = Config.loadPreference("images/characters/Boy-animations.xml");
		u.setName("Boy");
		u.setMove(4);
		u.setSpeed(60);
		u.setStrength(30);
		u.setDefence(10);
		u.setMaxHp(30);
		u.setImageData("images/characters/princess-animations.xml",ui);
		
//		IWeapon w = util.Util.getClassInstancebyName("custom.CustomSpear");
		IWeapon w = new Spear();
		w.setRange(3);
		w.setStrength(2);
		u.setWeapon(w);

		player.addUnit(u);

	}

	public Map startMap(String name){
		currentMap = new Map(name, player);
		return currentMap;
	}

	public IMap getCurrentMap() {
		return currentMap;
	}
	
}
