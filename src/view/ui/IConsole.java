package view.ui;

import java.awt.Graphics2D;

/**
 * @author Bilal Hussain
 */
public interface IConsole {

	// Adds a string to the Text area, the string will be printed on a new line
	void println(Object newObj);

	// Adds a string to the Text area, the string will be printed on a new line
	void printf(String format, Object... args);

	// Draw the text area at the specifed point, with the specifed width.
	void paint(Graphics2D graphics2D, int drawX, int drawY, int width);

	// Scroll backwards by one line
	void scrollUp();

	// Scroll forwards by one line
	void scrollDown();

	// Scroll backwards by one page
	void pageUp();

	// Scroll forwards by one page
	void pageDown();

	// Return the height in pixel of the text area
	int getHeight();

}