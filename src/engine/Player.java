package engine;

import java.util.ArrayList;

import common.interfaces.IUnit;


/**
 * @author bilalh
 */
public class Player {

	protected ArrayList<IUnit> units = new ArrayList<IUnit>();
	
	public Player(){
		
	}

	public void addUnit(IUnit u){
		units.add(u);
	}

	/** @category Generated */
	public ArrayList<IUnit> getUnits() {
		return units;
	}
	
}
