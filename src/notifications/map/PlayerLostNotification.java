package notifications.map;

import view.map.GuiMap;
import common.interfaces.IMapNotification;

/**
 * @author Bilal Hussain
 */
public class PlayerLostNotification  implements IMapNotification {

	@Override
	public void process(GuiMap map) {
		map.playerLost();
	}

	@Override
	public String readableInfo() {
		return "Player Lost";
	}

}
