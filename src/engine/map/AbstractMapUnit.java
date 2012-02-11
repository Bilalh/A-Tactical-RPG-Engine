package engine.map;

import java.util.UUID;

import common.Location;
import common.interfaces.ILocation;

import engine.unit.IMutableUnit;
import engine.unit.UnitImages;

/**
 * Keeps all the gets and setters so it does not clutter the mapUnit class
 * 
 * @author Bilal Hussain
 */
public abstract class AbstractMapUnit implements IMutableMapUnit {

	protected IMutableUnit unit;
	protected MapPlayer player;
	protected int gridX = -1;
	protected int gridY = -1;
	protected int currentHp;

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
		unit.setStrength(strength);
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

	@Override
	public int getGridX() {
		return gridX;
	}

	@Override
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	@Override
	public int getGridY() {
		return gridY;
	}

	@Override
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	@Override
	public int getCurrentHp() {
		return currentHp;
	}

	@Override
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	public IMutableUnit getUnit() {
		return unit;
	}

	@Override
	public void setSpeed(int speed) {
		unit.setSpeed(speed);
	}

	@Override
	public int getSpeed() {
		return unit.getSpeed();
	}

	@Override
	public MapPlayer getPlayer() {
		return player;
	}

	@Override
	public void setName(String name) {
		unit.setName(name);
	}

	@Override
	public UnitImages getImageData() {
		return unit.getImageData();
	}

	@Override
	public void setImageData(UnitImages imageData) {
		unit.setImageData(imageData);
	}

	@Override
	public int getMaxWeight() {
		return unit.getMaxWeight();
	}

	
}
