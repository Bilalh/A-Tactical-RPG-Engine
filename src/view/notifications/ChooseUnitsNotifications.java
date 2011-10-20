/**
 * 
 */
package view.notifications;

import java.util.ArrayList;

import common.interfaces.IMapNotification;
import common.interfaces.INotification;

import view.GuiMap;

import engine.Unit;

/**
 * @author bilalh
 */
public class ChooseUnitsNotifications implements IMapNotification {

	private ArrayList<Unit> units = new ArrayList<Unit>();
	private ArrayList<Unit> aiUnits = new ArrayList<Unit>();

	
	/** @category Generated Constructor */
	public ChooseUnitsNotifications(ArrayList<Unit> units, ArrayList<Unit> aiUnits) {
		this.units = units;
		this.aiUnits = aiUnits;
	}

	@Override
	public void process(GuiMap map) {
		map.chooseUnits(units, aiUnits);
	}

	/** @category Generated */
	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("ChooseUnitsNotifications [units=%s, aiUnits=%s]",
				units != null ? units.subList(0, Math.min(units.size(), maxLen)) : null,
				aiUnits != null ? aiUnits.subList(0, Math.min(aiUnits.size(), maxLen)) : null);
	}
	
}
