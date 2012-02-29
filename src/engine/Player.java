package engine;

import java.util.ArrayList;

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
	
}
