package notifications.map;

import common.interfaces.INotification;
import controller.MainController;

/**
 * @author Bilal Hussain
 */
public class MapFinishedNotification implements INotification<MainController> {

	@Override
	public void process(MainController mc) {
		System.out.println("MapFinishedNotification");
		mc.mapFinished();
	}
	
}
