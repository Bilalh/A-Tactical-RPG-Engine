package engine.map;

import java.util.UUID;

import common.interfaces.ILocation;

import engine.items.RangedWeapon;
import engine.map.ai.AbstractTargetOrdering;
import engine.unit.AiUnit;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
public class AIMapUnit extends MapUnit<AiUnit> {
	
	public AIMapUnit(AiUnit unit, ILocation l, MapPlayer ai) {
		super(unit, l, ai);
	}

	@Override
	public boolean isAI(){
		return true;
	}

	/** @category Generated */
	public AbstractTargetOrdering getOrdering() {
		return unit.getOrdering();
	}
	
	
	
}
