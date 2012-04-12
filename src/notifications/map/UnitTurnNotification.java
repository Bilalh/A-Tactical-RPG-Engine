package notifications.map;

import view.map.GuiMap;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.INotification;

/**
 * @author Bilal Hussain
 */
public class UnitTurnNotification implements IMapNotification {

	private IMapUnit unit;


	public UnitTurnNotification(IMapUnit unit) {
		this.unit = unit;
	}

	@Override
	public void process(GuiMap map) {
		map.unitsTurn(unit);
	}

	@Override
	public String toString() {
		return String.format("UnitTurnNotification [unit=%s]", unit);
	}

	@Override
	public String readableInfo() {
		return null;
	}
	
}
