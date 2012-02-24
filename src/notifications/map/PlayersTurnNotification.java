/**
 * 
 */
package notifications.map;

import common.interfaces.IMapNotification;
import common.interfaces.INotification;

import view.map.GuiMap;

/**
 * @category unused
 * @author bilalh
 */
public class PlayersTurnNotification implements IMapNotification {

	@Override
	public void process(GuiMap map) {
		map.playersTurn();
	}

	@Override
	public String readableInfo() {
		return "Player's Turn";
	}

}
