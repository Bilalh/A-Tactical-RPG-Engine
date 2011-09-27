/**
 * 
 */
package engine.notifications;

import java.util.ArrayList;

import engine.Unit;

/**
 * @author bilalh
 */
public class ChooseUnitsNotifications implements Notification {

	private ArrayList<Unit> units = new ArrayList<Unit>();

	
	/** @category Generated Constructor */
	public ChooseUnitsNotifications(ArrayList<Unit> units) {
		this.units = units;
	}


	/** @category Generated Getter */
	public ArrayList<Unit> getUnits() {
		return units;
	}
	
}
