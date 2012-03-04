package common.assets;

import java.util.*;

import org.apache.log4j.Logger;

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
	private static final Logger log = Logger.getLogger(AssertStore.class);
	
	private static AssertStore singleton = new AssertStore();
	private Map<UUID, IWeapon> weapons   = Collections.synchronizedMap(new HashMap<UUID, IWeapon>());
	private Map<UUID, ISkill>  skills    = Collections.synchronizedMap(new HashMap<UUID, ISkill>());

	public  IWeapon getWeapon(UUID id){
		final IWeapon w =  weapons.get(id);
		assert w != null : "Assert with id not found: " + id ;
		return w;
	}
	
	public  ISkill getSkill(UUID id){
		final ISkill s =  skills.get(id);
		assert s != null : "Assert with id not found: " + id ;
		return s;
	}
	
	public ArrayList<ISkill> getSkills(Collection<UUID> ids){
		ArrayList<ISkill> skills = new ArrayList<ISkill>();
		for (UUID id : ids) {
			skills.add(getSkill(id));
		}
		return skills;
	}
	
	public void loadAssets(AssetsLocations paths){
		weapons.clear();
		skills.clear();
		
		Weapons ws = Config.loadPreference(paths.weaponsPath);
		weapons.putAll(ws.getMap());
		
		Skills ss  = Config.loadPreference(paths.skillsPath);
		skills.putAll(ss.getMap());
	}

	
	// For editor 
	
	public void loadWeapons(Weapons w){
		weapons.clear();
		weapons.putAll(w.getMap());
		log.debug("Loaded weapons");
	}

	public void loadSkill(Skills s){
		skills.clear();
		skills.putAll(s.getMap());
		log.debug("Loaded skills");
	}

	
	private AssertStore() {
	}
	
	public static AssertStore instance() {
		return singleton;
	}
}
