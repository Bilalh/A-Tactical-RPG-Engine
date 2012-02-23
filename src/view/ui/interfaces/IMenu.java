package view.ui.interfaces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import view.ui.MenuItem;

/**
 * A menu has a number of items that can be chosen either using the keyboard or the mouse.
 * @author Bilal Hussain
 */
public interface IMenu extends IDisplayable {

	void scrollToPrevious();

	void scrollToNext();

	IMenuItem getClickedItem(Point p);

	void addCommand(MenuItem m);

	void setCommands(List<MenuItem> commands);

	void reset();

	void clearCommands();

	IMenuItem getSelected();

	int getSelectedIndex();

	int getWidth();
	void setWidth(int width);

}