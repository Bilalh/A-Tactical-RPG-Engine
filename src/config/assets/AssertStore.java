package config.assets;

import java.util.*;

import org.apache.log4j.Logger;

import common.gui.ResourceManager;
import common.gui.Sprite;
import common.interfaces.IWeapon;
import config.Config;
import engine.skills.ISkill;

/**
 * Keeps Track of all the item, skills.
 * 
 * @author Bilal Hussain
 */
public class AssertStore {
	private static final Logger log = Logger.getLogger(AssertStore.class);

	private static AssertStore singleton = new AssertStore();
	private Map<UUID, IWeapon> weapons = Collections.synchronizedMap(new HashMap<UUID, IWeapon>());
	private Map<UUID, ISkill> skills = Collections.synchronizedMap(new HashMap<UUID, ISkill>());
	private Map<UUID, MusicData> music = Collections.synchronizedMap(new HashMap<UUID, MusicData>());
	private Map<UUID, MusicData> sounds = Collections.synchronizedMap(new HashMap<UUID, MusicData>());

	public IWeapon getWeapon(UUID id) {
		final IWeapon w = weapons.get(id);
		assert w != null : "Weapon not found: " + id + "\n" + weapons;
		return w;
	}

	public MusicData getMusic(UUID id) {
		final MusicData w = music.get(id);
		assert w != null : "Music not found: " + id + "\n" + music;
		return w;
	}

	public MusicData getSound(UUID id) {
		final MusicData w = sounds.get(id);
		assert w != null : "Sound not found: " + id + "\n" + sounds;
		return w;
	}

	public ISkill getSkill(UUID id) {
		final ISkill s = skills.get(id);
		assert s != null : "Skill not found: " + id;
		return s;
	}

	public ArrayList<ISkill> getSkills(Collection<UUID> ids) {
		ArrayList<ISkill> skills = new ArrayList<ISkill>();
		for (UUID id : ids) {
			skills.add(getSkill(id));
		}
		return skills;
	}

	public void loadAssets(AssetsLocations paths) {
		weapons.clear();
		skills.clear();
		music.clear();
		sounds.clear();

		Weapons ws = Config.loadPreference(paths.weaponsPath);
		weapons.putAll(ws.getMap());

		Skills ss = Config.loadPreference(paths.skillsPath);
		skills.putAll(ss.getMap());

		music.putAll(Config.<Musics> loadPreference(paths.musicLocation).getMap());
		sounds.putAll(Config.<Musics> loadPreference(paths.soundsLocation).getMap());
	}

	// For editor

	public void loadWeapons(Weapons w) {
		weapons.clear();
		weapons.putAll(w.getMap());
		log.debug("Loaded weapons");
	}

	public void loadSkill(Skills s) {
		skills.clear();
		skills.putAll(s.getMap());
		log.debug("Loaded skills");
	}

	private AssertStore() {
	}

	public static AssertStore instance() {
		return singleton;
	}

	public Map<UUID, IWeapon> getWeapons() {
		return weapons;
	}
}
