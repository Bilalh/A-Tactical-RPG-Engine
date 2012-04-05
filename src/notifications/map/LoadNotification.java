package notifications.map;

import common.interfaces.INotification;
import controller.MainController;

/**
 * @author Bilal Hussain
 */
public class LoadNotification implements INotification<MainController> {

	@Override
	public void process(MainController mc) {
		mc.load();
	}

}
