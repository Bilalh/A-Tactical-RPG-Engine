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

	private ArrayList<Unit> aiUnits = new ArrayList<Unit>();

	
	/** @category Generated Constructor */
	public ChooseUnitsNotifications(ArrayList<Unit> units, ArrayList<Unit> aiUnits) {
		this.units = units;
		this.aiUnits = aiUnits;
	}

	/** @category Generated Getter */
	public ArrayList<Unit> getUnits() {
		return units;
	}

	/** @category Generated Getter */
	public ArrayList<Unit> getAiUnits() {
		return aiUnits;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("ChooseUnitsNotifications [units=%s, aiUnits=%s]",
				units != null ? units.subList(0, Math.min(units.size(), maxLen)) : null,
				aiUnits != null ? aiUnits.subList(0, Math.min(aiUnits.size(), maxLen)) : null);
	}
	
}
