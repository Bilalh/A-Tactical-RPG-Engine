package engine;

import common.interfaces.IUnit;

import java.util.*;

import engine.map.Tile;

/**
 * Store details about the unit.
 * @author bilalh
 */
public class Unit implements IMutableUnit {

	private String name;

	private int maxHp;
	private int move;
	
	private int strength;
	private int defence;
	
	private int speed;
	
	private int level;
	private int exp;

	final private UUID uuid;
	private int weight;
	String spriteSheetLocation;

	UnitImages imageData;
	
	public Unit(){
		uuid = UUID.randomUUID();
		this.name = uuid.toString();
		imageData = new UnitImages();
	}
	
	public Unit(String name, int maxHp, int move, int strength, int speed) {
		this();
		this.name=name;
		this.maxHp = maxHp;
		this.move = move;
		this.strength = strength;
		this.speed    = speed;
	}

	@Override
	public int getCost(Tile old, Tile next) {
		return 1 + Math.abs(next.getCost() - old.getCost());
	}

	@Override
	public UnitImageData getImageData(String ref){
		return imageData.get(ref);
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

	@Override
	public String toString() {
		return String.format("Unit [name=%s, maxHp=%s, move=%s, strength=%s, defence=%s, speed=%s, level=%s, exp=%s, uuid=%s, weight=%s]",
				name, maxHp, move, strength, defence, speed, level, exp, uuid, weight);
	}

	/** @category Generated */
	@Override
	public String getSpriteSheetLocation() {
		return spriteSheetLocation;
	}

	/** @category Generated */
	public void setSpriteSheetLocation(String spriteSheetLocation) {
		this.spriteSheetLocation = spriteSheetLocation;
	}

	/** @category Generated */
	@Override
	public void setName(String name) {
		this.name = name;
	}

}
