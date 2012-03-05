package common.assets;

import java.util.HashMap;
import java.util.UUID;

import com.sun.jdi.Location;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitPlacement")
public class UnitPlacement {

	private Units units;
	private HashMap<UUID, Location> unitPlacement;

	/** @category Generated */
	private UnitPlacement(Units units, HashMap<UUID, Location> unitPlacement) {
		this.units = units;
		this.unitPlacement = unitPlacement;
	}

	/** @category Generated */
	public Units getUnits() {
		return units;
	}

	/** @category Generated */
	public HashMap<UUID, Location> getUnitPlacement() {
		return unitPlacement;
	}

	/** @category Generated */
	public void setUnits(Units units) {
		this.units = units;
	}

	/** @category Generated */
	public void setUnitPlacement(HashMap<UUID, Location> unitPlacement) {
		this.unitPlacement = unitPlacement;
	}
	
}
