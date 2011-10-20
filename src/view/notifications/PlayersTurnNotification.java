/**
 * 
 */
package view.notifications;

import common.interfaces.IMapNotification;
import common.interfaces.INotification;

import view.GuiMap;

/**
 * @author bilalh
 */
public class PlayersTurnNotification implements IMapNotification {

	@Override
	public void process(GuiMap map) {
		// TODO process method
		
	}
	
	@Override
	public String toString() {
		return String.format("PlayersTurnNotification []");
	}

}
