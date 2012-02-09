/**
 * 
 */
package notifications;

import common.interfaces.IMapNotification;
import common.interfaces.INotification;

import view.map.GuiMap;

/**
 * @author bilalh
 */
public class PlayersTurnNotification implements IMapNotification {

	@Override
	public void process(GuiMap map) {
		map.playersTurn();
	}
	
	@Override
	public String toString() {
		return String.format("PlayersTurnNotification []");
	}

}
