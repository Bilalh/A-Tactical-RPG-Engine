package common.interfaces;

import view.map.GuiMap;

/**
 * Notifictions for the map.
 *
 * @author Bilal Hussain
 */
public interface IMapNotification extends INotification<GuiMap> {

	/**
	 * Return a string for logging.
	 */
	String readableInfo();

}
