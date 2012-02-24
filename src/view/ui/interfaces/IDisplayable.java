package view.ui.interfaces;

import java.awt.Graphics2D;

/**
 * An Object that can be drawn at a specifed location
 * @author Bilal Hussain
 */
public interface IDisplayable {

	/**
	 * Draw the object at the specifed location.
	 */
	void draw(Graphics2D g, int drawX, int drawY);
}
