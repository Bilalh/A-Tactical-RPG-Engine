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

	private ArrayList<IMutableUnit> units = new ArrayList<IMutableUnit>();
	
	public Player(){
		
	}

	public void addUnit(IMutableUnit u){
		getUnits().add(u);
	}

	public ArrayList<IMutableUnit> getUnits() {
		return units;
	}

	public boolean addUnits(Collection<? extends IMutableUnit> us) {
		return getUnits().addAll(us);
	}

	protected void setUnits(ArrayList<IMutableUnit> units) {
		this.units = units;
	}
	
}
