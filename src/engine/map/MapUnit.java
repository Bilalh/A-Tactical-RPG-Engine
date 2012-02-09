package engine.map;

import java.util.EnumSet;

import common.interfaces.ILocation;
import engine.IMutableUnit;
import engine.UnitImages;

import static engine.map.UnitStatus.*;

/**
 * Store data about unit specific to this map such as location on the map.
 * 
 * @author Bilal Hussain
 */
public class MapUnit extends AbstractMapUnit {

	protected int readiness = 100;
	protected EnumSet<UnitStatus> status = EnumSet.noneOf(UnitStatus.class);
	
	public MapUnit(IMutableUnit unit, ILocation l, MapPlayer player) {
		this.gridX = l.getX();
		this.gridY = l.getY();
		this.unit = unit;
		this.player = player;
	}

	@Override
	public boolean hasStatus(UnitStatus s) {
		return status.contains(s);
	}

	@Override
	public void setStatus(UnitStatus s) {
		status.add(s);
	}

	@Override
	public boolean isMoved() {
		return status.contains(UnitStatus.MOVED);
	}

	@Override
	public void setMoved() {
		status.add(MOVED);
	}

	@Override
	public int getMaxWeight() {
		return unit.getMaxWeight();
	}

	@Override
	public int getReadiness() {
		return readiness;
	}

	@Override
	public void setReadiness(int readiness) {
		this.readiness = readiness;
	}

	@Override
	public String toString() {
		return String.format("MapUnit [player=%s, gridX=%s, gridY=%s, currentHp=%s, readiness=%s, status=%s, getMove()=%s, getSpeed()=%s]",
				player, gridX, gridY, currentHp, readiness, status, getMove(), getSpeed());
	}
}
