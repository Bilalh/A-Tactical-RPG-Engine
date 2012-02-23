package engine.items;

import java.util.ArrayList;
import java.util.Collection;

import engine.map.interfaces.IMutableMapUnit;
import engine.unit.IWeapon;

/**
 * @author Bilal Hussain
 */
public abstract class AbstractWeapon  implements IWeapon {

	protected int strength = 1;
	protected int range    = 1;

	@Override
	public int getStrength() {
		return strength;
	}

	/** @category Generated */
	@Override
	public int getRange() {
		return range;
	}

	/** @category Generated */
	@Override
	public void setRange(int range) {
		this.range = range;
	}

	/** @category Generated */
	@Override
	public void setStrength(int strength) { 
		this.strength = strength;
	}

	@Override
	public Collection<IMutableMapUnit> getTarget(IMutableMapUnit attacker, IMutableMapUnit target) {
		ArrayList<IMutableMapUnit> list = new ArrayList<IMutableMapUnit>();
		list.add(target);
		return list;
	}

}