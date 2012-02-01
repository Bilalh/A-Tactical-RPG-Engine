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
	
	/** @category Generated */
	public Unit(String name, int maxHp, int move, int strength) {
		this(name, maxHp, move, strength, 10);
	}

	public Unit(String name){
		uuid = UUID.randomUUID();
		this.name = name;
	}
	
	/** @category Generated */
	public Unit(String name, int maxHp, int move, int strength, int speed) {
		this(name);
		this.maxHp = maxHp;
		this.move = move;
		this.strength = strength;
		this.speed    = speed;
	}

	@Override
	public int getCost(Tile old, Tile next) {
		return 1 + Math.abs(next.getCost() - old.getCost());
	}

	/** @category Generated Getter */
	@Override
	public String getName() {
		return name;
	}

	/** @category Generated Getter */
	@Override
	public int getMaxHp() {
		return maxHp;
	}

	/** @category Generated Setter */
	@Override
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	/** @category Generated Getter */
	@Override
	public int getMove() {
		return move;
	}

	/** @category Generated Setter */
	@Override
	public void setMove(int move) {
		this.move = move;
	}

	/** @category Generated Getter */
	@Override
	public int getStrength() {
		return strength;
	}

	/** @category Generated Setter */
	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/** @category Generated Getter */
	@Override
	public int getDefence() {
		return defence;
	}

	/** @category Generated Setter */
	@Override
	public void setDefence(int defence) {
		this.defence = defence;
	}

	/** @category Generated Getter */
	@Override
	public int getLevel() {
		return level;
	}

	/** @category Generated Setter */
	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	/** @category Generated Getter */
	@Override
	public int getExp() {
		return exp;
	}

	/** @category Generated Setter */
	@Override
	public void setExp(int exp) {
		this.exp = exp;
	}

	/** @category Generated Getter */
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

	/** @category Generated */
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

}
