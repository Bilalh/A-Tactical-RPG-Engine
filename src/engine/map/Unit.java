package engine.map;

import common.Location;
import java.util.*;

import javax.vecmath.Point2d;

import engine.PathfindingEx.Mover;

/**
 * @author bilalh
 */
public class Unit implements IModelUnit, Mover {

	private String name;

	private int maxHp;
	private int currentHp;

	private int move;
	private int strength;

	private int gridX;
	private int gridY;

	final private UUID uuid;

	/** @category Generated */
	public Unit(String name, int maxHp, int move, int strength) {
		this.name = name;
		this.maxHp = maxHp;
		this.currentHp = maxHp;
		this.move = move;
		this.strength = strength;
		uuid = UUID.randomUUID();
	}

	
	@Override
	public int getCost(Tile old, Tile next) {
		return 1 + Math.abs(next.getCost() - old.getCost());
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
	public int getCurrentHp() {
		return currentHp;
	}

	/** @category Generated */
	@Override
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
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
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public int getGridX() {
		return gridX;
	}

	/** @category Generated */
	@Override
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated */
	@Override
	public int getGridY() {
		return gridY;
	}

	/** @category Generated */
	@Override
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setLocation(Location p) {
		gridX = p.x;
		gridY = p.y;
	}

	@Override
	public Location getLocation() {
		return new Location(gridX, gridY);
	}

	@Override
	public String toString() {
		return String
				.format("Unit [name=%s, maxHp=%s, currentHp=%s, move=%s, strength=%s, gridX=%s, gridY=%s, uuid=%s]",
						name, maxHp, currentHp, move, strength, gridX, gridY, uuid);
	}
}
