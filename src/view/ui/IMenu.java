package view.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * A menu has a number of items that can be chosen either using the keyboard or the mouse.
 * @author Bilal Hussain
 */
public interface IMenu extends IDisplayable {

	void scrollToPrevious();

	void scrollToNext();

	MenuItem getClickedItem(Point p);

	void addCommand(MenuItem m);

	void setCommands(List<MenuItem> commands);

	void reset();

	void clear();

	MenuItem getSelected();

}