package engine.unit;

import java.util.ArrayList;
import java.util.UUID;

import view.ui.MenuItem;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.enums.Orientation;
import common.interfaces.IWeapon;
import config.Config;
import config.assets.AssetStore;

import engine.items.MeleeWeapon;
import engine.items.RangedWeapon;
import engine.map.Tile;
import engine.skills.ISkill;
import engine.skills.RangedSkill;

/**
 * Store details about the unit.
 * @author bilalh
 */
@XStreamAlias("unit")
public class Unit implements IMutableUnit {
	@XStreamAsAttribute
	private final UUID uuid;
	
	private String name;

	private int maxHp    = 1;
	private int move     = 1;
	
	private int strength = 1;
	private int defence  = 1;	
	private int speed    = 1;
	
	private int level    = 1;
	private int exp      = 0;

	private String imageDataRef;
	private transient SpriteSheetData imageData;
	
	// Only store the ids of the skill and weapons when saving. 
	private UUID wepaonId;
	private ArrayList<UUID> skillIds;
	
	private transient IWeapon weapon;
	private transient ArrayList<ISkill> skills;
	
	public Unit(){
		uuid   = UUID.randomUUID();
		name   = uuid.toString();
		setSkills(new ArrayList<ISkill>());
		level = 1;
		exp   = 0;
	}

	public Unit(String name, int maxHp, int move, int strength, int speed) {
		this();
		this.name   = name;
		this.maxHp  = maxHp;
		this.move   = move;
		this.speed  = speed;
		
		this.strength = strength;
	}

	
	public Unit(IMutableUnit u) {
		this();
		this.name     = u.getName();
		this.maxHp    = u.getMaxHp();
		this.move     = u.getMove();
		this.strength = u.getStrength();
		this.defence  = u.getDefence();
		this.speed    = u.getSpeed();
		this.level    = u.getLevel();
		this.exp      = u.getExp();
		setWeapon(u.getWeapon());
		setImageData(u.getImageData().getAnimationPath(), u.getImageData());
		setSkills((ArrayList<ISkill>) u.getSkills().clone());
	}

	// to give default values
	private Object readResolve() {
		if (imageData == null){
			imageData = Config.loadPreference(imageDataRef);
			assert imageData != null : "imageData not found "  + imageDataRef;
		}
		if (weapon == null){
			weapon = AssetStore.instance().getWeapon(wepaonId);
		}
		if (skills == null){
			skills = AssetStore.instance().getSkills(skillIds);
		}
		
		return this;
	}
	
	@Override
	public int getCost(Tile old, Tile next) {
		if   (next.getOrientation() == Orientation.EMPTY) return Integer.MAX_VALUE;
		else return 1 + Math.abs(next.getCost() - old.getCost());
	}

	@Override
	public void setImageData(String ref, SpriteSheetData imageData) {
		this.imageDataRef = ref;
		this.imageData = imageData;
	}
	

	@Override
	public void setSkills(ArrayList<ISkill> skills) {
		this.skills = skills;
		skillIds = new ArrayList<UUID>();
		for (ISkill s : skills) {
			skillIds.add(s.getUuid());
		}
	}
	

	@Override
	public ArrayList<ISkill> getSkills() {
		return skills;
	}
	

	@Override
	public String getName() {
		return name;
	}


	@Override
	public int getMaxHp() {
		return maxHp;
	}


	@Override
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}


	@Override
	public int getMove() {
		return move;
	}


	@Override
	public void setMove(int move) {
		this.move = move;
	}


	@Override
	public int getStrength() {
		return strength;
	}


	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}


	@Override
	public int getDefence() {
		return defence;
	}


	@Override
	public void setDefence(int defence) {
		this.defence = defence;
	}


	@Override
	public int getLevel() {
		return level;
	}


	@Override
	public void setLevel(int level) {
		this.level = level;
	}


	@Override
	public int getExp() {
		return exp;
	}


	@Override
	public void setExp(int exp) {
		this.exp = exp;
	}


	@Override
	public UUID getUuid() {
		return uuid;
	}


	@Override
	public int getSpeed() {
		return speed;
	}
	

	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}


	@Override
	public void setName(String name) {
		this.name = name;
	}


	@Override
	public SpriteSheetData getImageData() {
		return imageData;
	}


	@Override
	public IWeapon getWeapon() {
		return weapon;
	}


	@Override
	public void setWeapon(IWeapon weapon) {
		this.weapon   = weapon;
		this.wepaonId = weapon.getUuid();
	}

	
//	@Override
//	public String toString() {
//		return String.format("Unit [name=%s, maxHp=%s, move=%s, strength=%s, defence=%s, speed=%s, level=%s, exp=%s, uuid=%s, weight=%s]",
//				name, maxHp, move, strength, defence, speed, level, exp, uuid, weight);
//	}

	@Override
	public String toString() {
		return String
				.format("Unit [name=%s, maxHp=%s, move=%s, strength=%s, defence=%s, speed=%s, level=%s, exp=%s, uuid=%s, imageDataRef=%s, imageData=%s weapon=%s, skills=%s]",
						name, maxHp, move, strength, defence, speed, level, exp, uuid, imageDataRef, imageData, weapon, skills);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Unit)) return false;
		Unit other = (Unit) obj;
		if (uuid == null) {
			if (other.uuid != null) return false;
		} else if (!uuid.equals(other.uuid)) return false;
		return true;
	}

}
