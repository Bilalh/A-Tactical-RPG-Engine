package view.ui;

import java.awt.Graphics2D;

/**
 * @author Bilal Hussain
 */
public interface IConsole extends IDisplayable {

	// Adds a string to the Text area, the string will be printed on a new line
	void println(Object newObj);

	// Adds a string to the Text area, the string will be printed on a new line
	void printf(String format, Object... args);

	// Draw the text area at the specifed point, with the specifed width.
	@Override
	void draw(Graphics2D g, int drawX, int drawY);
	
	// Scroll backwards by one line
	void scrollUp();

	// Scroll forwards by one line
	void scrollDown();

	// Scroll backwards by one page
	void pageUp();

	// Scroll forwards by one page
	void pageDown();

	// Return the height in xel of the text area
	int getHeight();

	int getWidth();

	void setWidth(int width);
	
}