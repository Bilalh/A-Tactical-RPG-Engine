package engine;

import java.util.ArrayList;
import java.util.Collection;

import common.interfaces.IUnit;
import engine.unit.IMutableUnit;


/**
 * The player has a number of units.
 * @author Bilal Hussain
 */
public class Player {

	protected ArrayList<IMutableUnit> units = new ArrayList<IMutableUnit>();
	
	public Player(){
		
	}

	public void addUnit(IMutableUnit u){
		units.add(u);
	}

	public ArrayList<IMutableUnit> getUnits() {
		return units;
	}

	public boolean addUnits(Collection<? extends IMutableUnit> us) {
		return units.addAll(us);
	}
	
}
