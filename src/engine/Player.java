package engine;

import java.util.ArrayList;
import java.util.Collection;

import common.interfaces.IUnit;
import engine.unit.IMutableUnit;


/**
 * @author bilalh
 */
public class Player {

	protected ArrayList<IMutableUnit> units = new ArrayList<IMutableUnit>();
	
	public Player(){
		
	}

	public void addUnit(IMutableUnit u){
		units.add(u);
	}

	/** @category Generated */
	public ArrayList<IMutableUnit> getUnits() {
		return units;
	}

	/** @category Generated */
	public boolean addUnits(Collection<? extends IMutableUnit> us) {
		return units.addAll(us);
	}
	
}
