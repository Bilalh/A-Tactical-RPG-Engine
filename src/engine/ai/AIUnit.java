package engine.ai;

import java.util.UUID;

import engine.map.MapPlayer;
import engine.map.MapUnit;

/**
 * @author Bilal Hussain
 */
public class AIUnit extends MapUnit {

	final private String name;

	private int maxHp;
	private int move;
	private int strength;
	private int defence;

	private int level;
	private int exp;

	final private UUID uuid;

	private int speed;

	public AIUnit(String name, int gridX, int gridY, MapPlayer ai) {
		super(gridX, gridY);
		this.name = name;
		uuid = UUID.randomUUID();
		this.player = ai;
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
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public int getSpeed(){
		return speed;
	}
	
	
}
