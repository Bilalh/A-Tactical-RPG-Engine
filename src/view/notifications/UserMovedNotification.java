package view.notifications;

import view.GuiMap;
import common.interfaces.IMapNotification;

import engine.Unit;

/**
 * @author Bilal Hussain
 */
public class UserMovedNotification  implements IMapNotification {
	
	private Unit u;
	
	
	/** @category Generated Constructor */
	public UserMovedNotification(Unit u) {
		this.u = u;
	}

	@Override
	public void process(GuiMap obj) {
		obj.unitMoved(u);
	}

	@Override
	public String toString() {
		return String.format("UserMovedNotification [u=%s]", u);
	}
	
}
