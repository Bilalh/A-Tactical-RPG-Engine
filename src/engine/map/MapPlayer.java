/**
 * 
 */
package engine.map;

import java.util.ArrayList;

import common.interfaces.IMapUnit;
import engine.unit.Unit;

/**
 * @author bilalh
 */
public class MapPlayer {
	
	protected ArrayList<IMutableMapUnit> units;

	/** @category Generated Constructor */
	public MapPlayer(ArrayList<IMutableMapUnit> unit) {
		this.units = unit;
	}

	/** @category Generated */
	public ArrayList<IMutableMapUnit> getUnits() {
		return units;
	}

}
