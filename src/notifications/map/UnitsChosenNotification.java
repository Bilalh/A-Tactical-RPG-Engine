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


	public UnitsChosenNotification(ArrayList<IMapUnit> units) {
		this.units = units;
	}

	@Override
	public void process(GuiMap map) {
		map.unitsChoosen(units);
	}

	@Override
	public String readableInfo() {
		StringBuffer b = new StringBuffer(units.size() *15);
		b.append("Units Choosen: ");
		for (int i = 0; i < units.size(); i++) {
			b.append(units.get(i).getName());
			if (i != units.size()-1){
				b.append(", ");
			}
		}
		return b.toString();
	}

}
