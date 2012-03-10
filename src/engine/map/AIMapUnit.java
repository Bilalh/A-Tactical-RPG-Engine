package engine.map;

import java.util.UUID;

import common.interfaces.ILocation;

import engine.items.RangedWeapon;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
public class AIMapUnit extends MapUnit {

	public AIMapUnit(IMutableUnit unit, ILocation l, MapPlayer ai) {
		super(unit, l, ai);
	}

	@Override
	public boolean isAI(){
		return true;
	}
	
}
