package notifications.map;

import java.util.ArrayList;

import view.map.GuiMap;

import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.INotification;

/**
 * @author Bilal Hussain
 */
public class UnitsChosenNotification implements IMapNotification {

	private ArrayList<IMapUnit> units;

	/** @category Generated */
	public UnitsChosenNotification(ArrayList<IMapUnit> units) {
		this.units = units;
	}

	@Override
	public void process(GuiMap map) {
		map.unitsChoosen(units);
	}

}
