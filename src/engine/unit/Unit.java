package engine.unit;

import java.util.ArrayList;
import java.util.UUID;

import view.ui.MenuItem;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.enums.Orientation;
import common.interfaces.IWeapon;
import config.Config;

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

	private String name;

	private int maxHp;
	private int move;
	
	private int strength;
	private int defence;	
	private int speed;
	
	private int level;
	private int exp;

	@XStreamAsAttribute
	private final UUID uuid;
	private String imageDataRef;
	private transient UnitImages imageData;
	
	private int weight;
	private IWeapon weapon;
	
	private ArrayList<ISkill> skills;
	
	public Unit(){
		uuid = UUID.randomUUID();
		this.name = uuid.toString();
		imageData = new UnitImages();
		weapon    = new MeleeWeapon(1);
		skills    = new ArrayList<ISkill>();
		skills.add(new RangedSkill("Air Blade",     10, 2,  0, true,false));
		skills.add(new RangedSkill("Thunder Flare", 15, 3,  1, true,false));
		skills.add(new RangedSkill("Thunderbird",   40, 4,  2, true,false));
	}

	public Unit(String name, int maxHp, int move, int strength, int speed) {
		this();
		this.name   = name;
		this.maxHp  = maxHp;
		this.move   = move;
		this.speed  = speed;
		
		this.strength = strength;
	}

	// to give default values
	private Object readResolve() {
		if (imageData == null){
			imageData =Config.loadPreference(imageDataRef);
		}
		return this;
	}
	
	@Override
	public int getCost(Tile old, Tile next) {
		if   (next.getOrientation() == Orientation.EMPTY) return Integer.MAX_VALUE;
		else return 1 + Math.abs(next.getCost() - old.getCost());
	}

	@Override
	public void setImageData(String ref, UnitImages imageData) {
		this.imageDataRef = ref;
		this.imageData = imageData;
	}
	
	/** @category Generated */
	@Override
	public ArrayList<ISkill> getSkills() {
		return skills;
	}

	/** @category Generated */
	@Override
	public void setSkills(ArrayList<ISkill> skills) {
		this.skills = skills;
	}
	
	/** @category Generated */
	@Override
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public int getMaxHp() {
		return maxHp;
	}

	/** @category Generated */
	@Override
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	/** @category Generated */
	@Override
	public int getMove() {
		return move;
	}

	/** @category Generated */
	@Override
	public void setMove(int move) {
		this.move = move;
	}

	/** @category Generated */
	@Override
	public int getStrength() {
		return strength;
	}

	/** @category Generated */
	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/** @category Generated */
	@Override
	public int getDefence() {
		return defence;
	}

	/** @category Generated */
	@Override
	public void setDefence(int defence) {
		this.defence = defence;
	}

	/** @category Generated */
	@Override
	public int getLevel() {
		return level;
	}

	/** @category Generated */
	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	/** @category Generated */
	@Override
	public int getExp() {
		return exp;
	}

	/** @category Generated */
	@Override
	public void setExp(int exp) {
		this.exp = exp;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

	/** @category Generated */
	@Override
	public int getSpeed() {
		return speed;
	}
	
	/** @category Generated */
	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/** @category Generated */
	@Override
	public int getMaxWeight() {
		return weight;
	}

	/** @category Generated */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/** @category Generated */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/** @category Generated */
	@Override
	public UnitImages getImageData() {
		return imageData;
	}

	/** @category Generated */
	@Override
	public IWeapon getWeapon() {
		return weapon;
	}

	/** @category Generated */
	@Override
	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}


//	@Override
//	public String toString() {
//		return String.format("Unit [name=%s, maxHp=%s, move=%s, strength=%s, defence=%s, speed=%s, level=%s, exp=%s, uuid=%s, weight=%s]",
//				name, maxHp, move, strength, defence, speed, level, exp, uuid, weight);
//	}

	@Override
	public String toString() {
		return String
				.format("Unit [name=%s, maxHp=%s, move=%s, strength=%s, defence=%s, speed=%s, level=%s, exp=%s, uuid=%s, imageDataRef=%s, imageData=%s, weight=%s, weapon=%s, skills=%s]",
						name, maxHp, move, strength, defence, speed, level, exp, uuid, imageDataRef, imageData, weight, weapon, skills);
	}

	
	
}
