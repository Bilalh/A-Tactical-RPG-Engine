package engine.map;

import java.util.EnumSet;
import java.util.UUID;

import common.Location;
import common.interfaces.ILocation;
import engine.IMutableUnit;

import static engine.map.UnitStatus.*;

/**
 * Store data about unit specific to this map such as location on the map.
 * @author Bilal Hussain
 */
public class MapUnit implements IMutableMapUnit {

	protected IMutableUnit unit;
	protected MapPlayer player;
	
	protected int gridX = -1;
	protected int gridY = -1;

	protected int currentHp;
	
	protected int readiness = 100;
	
	protected EnumSet<UnitStatus> status  = EnumSet.noneOf(UnitStatus.class);
	
	public MapUnit(IMutableUnit unit, ILocation l, MapPlayer player) {
		this(l.getX(),l.getY());
		this.unit = unit;
		this.player = player;
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
	public boolean hasStatus(UnitStatus s){
		return status.contains(s);
	}
	
	@Override
	public void setStatus(UnitStatus s){
		status.add(s);
	}

	@Override
	public boolean isMoved(){
		return status.contains(UnitStatus.MOVED);
	}

	@Override
	public void setMoved(){
		status.add(MOVED);
	}

	/* ** Getters and Setters ** */
	
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

	/** @category Generated */
	@Override
	public void setSpeed(int speed) {
		unit.setSpeed(speed);
	}

	/** @category Generated */
	@Override
	public int getSpeed() {
		return unit.getSpeed();
	}

	/** @category Generated */
	@Override
	public MapPlayer getPlayer() {
		return player;
	}

	@Override
	public int getMaxWeight() {
		return unit.getMaxWeight();
	}

	/** @category Generated */
	@Override
	public int getReadiness() {
		return readiness;
	}



	/** @category Generated */
	public void setReadiness(int readiness) {
		this.readiness = readiness;
	}
	
	@Override
	public String toString() {
		return String.format("MapUnit [player=%s, gridX=%s, gridY=%s, currentHp=%s, readiness=%s, status=%s, getMove()=%s, getSpeed()=%s]",
				player, gridX, gridY, currentHp, readiness, status, getMove(), getSpeed());
	}
	
}
