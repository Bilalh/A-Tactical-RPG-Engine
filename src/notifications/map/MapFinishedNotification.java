package notifications.map;

import common.interfaces.INotification;
import controller.MainController;

/**
 * @author Bilal Hussain
 */
public class MapFinishedNotification implements INotification<MainController> {

	boolean won;

	public MapFinishedNotification(boolean won) {
		this.won = won;
	}

	@Override
	public void process(MainController mc) {
		System.out.println("MapFinishedNotification");
		mc.mapFinished(won);
	}

}
