package engine;

import java.util.UUID;

/**
 * @author bilalh
 */
public class Unit {

	private String name;
	
	private int maxHp;
	private int currentHp;
	
	private int move;
	private int strength;

	private int gridX;
	private int gridY;
	
	final private Long uuid;
	
	/** @category Generated Constructor */
	public Unit(String name, int maxHp, int move, int strength) {
		this.name = name;
		this.maxHp = maxHp;
		this.currentHp = maxHp;
		this.move = move;
		this.strength = strength;
		uuid = UUID.randomUUID().getMostSignificantBits();
	}
	
	/** @category Generated Getter */
	public int getMaxHp() {
		return maxHp;
	}
	/** @category Generated Setter */
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}
	/** @category Generated Getter */
	public int getCurrentHp() {
		return currentHp;
	}
	/** @category Generated Setter */
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}
	/** @category Generated Getter */
	public int getMove() {
		return move;
	}
	/** @category Generated Setter */
	public void setMove(int move) {
		this.move = move;
	}
	/** @category Generated Getter */
	public int getStrength() {
		return strength;
	}
	/** @category Generated Setter */
	public void setStrength(int strength) {
		this.strength = strength;
	}
	/** @category Generated Getter */
	public String getName() {
		return name;
	}

	/** @category Generated Getter */
	public int getGridX() {
		return gridX;
	}

	/** @category Generated Setter */
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated Getter */
	public int getGridY() {
		return gridY;
	}

	/** @category Generated Setter */
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	/** @category Generated Getter */
	public Long getUuid() {
		return uuid;
	}
	
}
