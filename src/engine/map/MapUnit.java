package engine.map;

import java.util.UUID;

import common.Location;
import common.interfaces.ILocation;
import engine.IMutableUnit;

/**
 * Store data about unit specific to this map such as location on the map.
 * @author Bilal Hussain
 */
public class MapUnit implements IMutableMapUnit {

	IMutableUnit unit;

	protected int gridX = -1;
	protected int gridY = -1;

	private int currentHp;

	/** @category Generated */
	public MapUnit(IMutableUnit unit, ILocation l) {
		this(l.getX(),l.getY());
		this.unit = unit;
	}

	protected MapUnit(int gridX, int gridY) {
		this.gridX = gridX;
		this.gridY = gridY;
	}
	
	@Override
	public int getCost(Tile old, Tile next) {
		return unit.getCost(old, next);
	}

	@Override
	public String getName() {
		return unit.getName();
	}

	@Override
	public int getMaxHp() {
		return unit.getMaxHp();
	}

	@Override
	public void setMaxHp(int maxHp) {
		unit.setMaxHp(maxHp);
	}

	@Override
	public int getMove() {
		return unit.getMove();
	}

	@Override
	public void setMove(int move) {
		unit.setMove(move);
	}

	@Override
	public int getStrength() {
		return unit.getStrength();
	}

	@Override
	public void setStrength(int strength) {
		
	}

	@Override
	public int getDefence() {
		return unit.getDefence();
	}

	@Override
	public void setDefence(int defence) {
		unit.setDefence(defence);
	}

	@Override
	public int getLevel() {
		return unit.getLevel();
	}

	@Override
	public void setLevel(int level) {
		unit.setLevel(level);
	}

	@Override
	public int getExp() {
		return unit.getExp();
	}

	@Override
	public void setExp(int exp) {
		unit.setExp(exp);
	}

	@Override
	public UUID getUuid() {
		return unit.getUuid();
	}
	
	@Override
	public void setLocation(ILocation p) {
		gridX = p.getX();
		gridY = p.getY();
	}

	@Override
	public Location getLocation() {
		return new Location(gridX, gridY);
	}

	/** @category Generated Getter */
	@Override
	public int getGridX() {
		return gridX;
	}

	/** @category Generated Setter */
	@Override
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated Getter */
	@Override
	public int getGridY() {
		return gridY;
	}

	/** @category Generated Setter */
	@Override
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	/** @category Generated Getter */
	@Override
	public int getCurrentHp() {
		return currentHp;
	}

	/** @category Generated Setter */
	@Override
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	/** @category Generated Getter */
	public IMutableUnit getUnit() {
		return unit;
	}

}
