package engine;

import java.util.ArrayList;

/**
 * @author bilalh
 */
public class Player {

	protected ArrayList<Unit> units = new ArrayList<Unit>();
	
	public Player(){
		
	}

	public void addUnit(Unit u){
		units.add(u);
	}

	/** @category Generated Getter */
	public ArrayList<Unit> getUnits() {
		return units;
	}
	
}
