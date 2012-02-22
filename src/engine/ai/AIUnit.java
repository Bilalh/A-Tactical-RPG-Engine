package engine.ai;

import java.util.UUID;

import common.interfaces.ILocation;

import engine.items.RangedWeapon;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
public class AIUnit extends MapUnit {

	public AIUnit(IMutableUnit unit, ILocation l, MapPlayer ai) {
		super(unit, l, ai);
	}

	@Override
	public boolean isAI(){
		return true;
	}
	
}
