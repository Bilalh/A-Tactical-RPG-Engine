package engine.map;

import static engine.map.interfaces.UnitStatus.MOVED;

import java.util.Collection;
import java.util.EnumSet;

import common.Location;
import common.interfaces.ILocation;

import engine.items.RangedWeapon;
import engine.map.interfaces.IMutableMapUnit;
import engine.map.interfaces.UnitStatus;
import engine.unit.IMutableUnit;

/**
 * Store data about unit specific to this map such as location on the map.
 * 
 * @author Bilal Hussain
 */
public class MapUnit<E extends IMutableUnit> extends AbstractMapUnit<E> {

	protected int readiness = 100;
	protected EnumSet<UnitStatus> status = EnumSet.noneOf(UnitStatus.class);

	public MapUnit(E unit, ILocation l, MapPlayer player) {
		this.gridX  = l.getX();
		this.gridY  = l.getY();
		this.unit   = unit;
		this.player = player;
		
		this.currentHp = getMaxHp();
	}

	@Override
	public int getCost(Tile old, Tile next) {

		// Can't go though another player's units
		IMutableMapUnit uu = next.getCurrentUnit();
		if (uu != null && uu.getPlayer() != getPlayer()) {
			return Integer.MAX_VALUE;
		}
		return unit.getCost(old, next);
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
	public int getReadiness() {
		return readiness;
	}

	@Override
	public void setReadiness(int readiness) {
		this.readiness = readiness;
	}

	@Override
	public String toString() {
		return String.format("MapUnit[name=%s, hp=%s/%s, move=%s, str=%s, def=%s, L=%s, speed=%s, rs=%s, status=%s]",
						getName(), getCurrentHp(), getMaxHp(), getMove(), getStrength(), getDefence(), getLocation(), getSpeed(),
						readiness, status);
	}

}
