package engine.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import common.gui.ResourceManager;
import common.gui.Sprite;
import common.interfaces.IWeapon;
import config.Config;
import engine.skills.ISkill;

/**
 * Keeps Track of all the item, skills.
 * @author Bilal Hussain
 */
public class AssertStore {

	private static AssertStore singleton = new AssertStore();
	private Map<UUID, IWeapon> weapons = Collections.synchronizedMap(new HashMap<UUID, IWeapon>());
	private Map<UUID, ISkill> skills   = Collections.synchronizedMap(new HashMap<UUID, ISkill>());

	public  IWeapon getWeapon(UUID id){
		final IWeapon w =  weapons.get(id);
		assert w != null : "Assert with id not found: " + id ;
		return w;
	}
	
	public  ISkill getSkills(UUID id){
		final ISkill s =  skills.get(id);
		assert s != null : "Assert with id not found: " + id ;
		return s;
	}
	
	
	public void loadAssets(AssetsLocations paths){
		Weapons ws = Config.loadPreference(paths.weaponsPath);
		weapons.putAll(ws.getMap());
	}
	
	private AssertStore() {
	}
	
	public static AssertStore instance() {
		return singleton;
	}
}
