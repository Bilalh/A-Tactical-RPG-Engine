package view.ui.interfaces;

import java.awt.Graphics2D;
import java.io.PrintStream;

/**
 * A Console displays uneditable text graphically to the user. paint has to be called to draw the console
 * The Console has a finite history which can scroll backwards and fowards.
 * @author Bilal Hussain
 */
public interface IConsole extends IDisplayable {


	/**
	 * Adds a string to the Text area, the string will be printed on a new line
	 * Note: Escape sequences such as \n are not honoured.
	 */
	void println(Object newObj);

	/**
	 * Adds a string to the Text area, the string will be printed on a new line
	 * Note: Escape sequences such as \n are not honoured.
	 */
	void printf(String format, Object... args);

	/** Draw the text area at the specifed point, with the specifed width. */
	@Override
	void draw(Graphics2D g, int drawX, int drawY);

	/**
	 * Scroll backwards by one line
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	void scrollUp();

	/**
	 * Scroll forwards by one line
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	void scrollDown();

	/**
	 * Scroll backwards by one page
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input. 
	 */
	void pageUp();

	/**
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	void pageDown();

	/**
	 * Returns the height in pixels of the text area.
	 */
	int getHeight();

	/**
	 * Returns the width in pixels of the text area.
	 */
	int getWidth();

	/**
	 * Sets the width of the console
	 */
	void setWidth(int width);

	/**
	 * Set the extra stream to print to
	 */
	void setPrintStream(PrintStream out);
	
}