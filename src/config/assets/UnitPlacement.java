package config.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import common.Location;
import config.IPreference;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitPlacement")
public class UnitPlacement implements IPreference {

	private Units units;
	private HashMap<UUID, Location> placement;

	public UnitPlacement(Units units, HashMap<UUID, Location> placement) {
		this.units     = units;
		this.placement = placement;
	}

	public IMutableUnit getUnit(UUID uuid){
		return units.get(uuid);
	}
	
	/** @category Generated */
	public Units getUnits() {
		return units;
	}

	/** @category Generated */
	public HashMap<UUID, Location> getUnitPlacement() {
		return placement;
	}

	/** @category Generated */
	public void setUnits(Units units) {
		this.units = units;
	}

	/** @category Generated */
	public void setUnitPlacement(HashMap<UUID, Location> unitPlacement) {
		this.placement = unitPlacement;
	}
	
}
