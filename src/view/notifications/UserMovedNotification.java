package view.notifications;

import view.GuiMap;
import common.interfaces.IMapNotification;
import common.interfaces.IUnit;


/**
 * @author Bilal Hussain
 */
public class UserMovedNotification  implements IMapNotification {
	
	private IUnit u;
	
	
	/** @category Generated */
	public UserMovedNotification(IUnit u) {
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
