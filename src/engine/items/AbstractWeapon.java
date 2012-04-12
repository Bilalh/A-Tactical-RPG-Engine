package engine.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import common.interfaces.IWeapon;

import engine.map.Map;
import engine.map.interfaces.IMutableMapUnit;
import engine.skills.AbstractUnitObject;

/**
 * Provide reasonable defaults for a weapon
 * @author Bilal Hussain
 */
public abstract class AbstractWeapon extends AbstractUnitObject  implements IWeapon {

	protected int strength;
	protected int range;
	
	protected String imageRef;

	
	public AbstractWeapon(){
		strength = 1;
		range    = 1;
		imageRef = "0-1";
	}

	@Override
	public Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, IMutableMapUnit target, Map map) {
		ArrayList<IMutableMapUnit> list = new ArrayList<IMutableMapUnit>();
		list.add(target);
		return list;
	}
	
	@Override
	public int getStrength() {
		return strength;
	}


	@Override
	public int getRange() {
		return range;
	}


	@Override
	public void setRange(int range) {
		this.range = range;
	}


	@Override
	public void setStrength(int strength) { 
		this.strength = strength;
	}


	@Override
	public String getImageRef() {
		return imageRef;
	}


	@Override
	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}


}