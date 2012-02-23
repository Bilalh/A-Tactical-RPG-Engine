package engine.items;

import java.util.ArrayList;
import java.util.Collection;

import engine.map.Map;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public abstract class AbstractWeapon  implements IWeapon {

	protected int strength = 1;
	protected int range    = 1;

	protected String imageRef = "0-1";


	@Override
	public Collection<IMutableMapUnit> getTarget(IMutableMapUnit attacker, IMutableMapUnit target, Map map) {
		ArrayList<IMutableMapUnit> list = new ArrayList<IMutableMapUnit>();
		list.add(target);
		return list;
	}
	
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

	/** @category Generated */
	@Override
	public String getImageRef() {
		return imageRef;
	}

	/** @category Generated */
	@Override
	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

}