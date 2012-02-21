/**
 * 
 */
package engine.map;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import util.Logf;

import common.interfaces.IMapUnit;
import engine.unit.Unit;

/**
 * @author bilalh
 */
public class MapPlayer {
	private static final Logger log = Logger.getLogger(MapPlayer.class);
	protected ArrayList<IMutableMapUnit> units;

	/** @category Generated */
	public ArrayList<IMutableMapUnit> getUnits() {
		return units;
	}

	/** @category Generated */
	public void setUnits(ArrayList<IMutableMapUnit> units) {
		this.units = units;
	}

	public void unitDied(IMutableMapUnit u){
		Logf.info(log, "%s died", u);
		assert units.contains(u) : u + "should be in units";
		units.remove(u);
	}
	
}
