package view.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.apache.log4j.Logger;

/**
 * A Console displays uneditable text graphically to the user. Draw has to be called to draw the textarea
 * The textArea has a finite history which can scroll backwards and fowards. The number of lines shown is 
 * specifed at creation and the resulting height of the textarea can be found using getHeight.
 * 
 * @author Bilal Hussain
 */
public class Console implements IConsole {

	private static final Logger log = Logger.getLogger(Console.class);

	// lines is a circular list
	private String[] lines;
	// index is the next free postion,
	private int index;
	// head is the index in lines to print from
	private int head;
	// Number of lines to show
	private final int numberOfLinesToShow;
	// Height in pixels
	private final int height;
	// Width in pixels
	private int width;

	// To make sure only the last n lines are shown
	private boolean around;
	// lines are numbered if true
	private boolean numberLines;
	private int lineIndex;

	private boolean printToSysout;

	public Console() {
		this(1000, 7, true);
	}

	public Console(int history, int linesToShow, boolean numberLines) {
		if (linesToShow <= 0 || linesToShow > history || history <= 0) throw new IllegalArgumentException("Invaild Arguments");
		
		lines = new String[history];
		index = head = lineIndex = 0;
		around = false;
		printToSysout = true;
		height = (int) ((17f) * linesToShow);
		numberOfLinesToShow = linesToShow;
	}

	// Adds a string to the Text area, the string will be printed on a new line
	// Note: Escape sequences such as \n are not honoured.
	@Override
	public void println(Object newObj) {
		if (newObj == null) throw new IllegalArgumentException("String can not be null");

		String newString = newObj.toString();
		if (printToSysout) log.info(newObj);

		lines[index] = numberLines ? newString : (lineIndex++) + " " + newString;

		index++;
		if (index == lines.length) around = true;
		index %= lines.length;

		// to make the order of lines work in all cases
		if (around) {
			head = (index + lines.length - numberOfLinesToShow) % lines.length;
		} else {
			head = index - numberOfLinesToShow;
			if (head < 0) head = 0;
		}
	}

	// Adds a string to the Text area, the string will be printed on a new line
	// Note: Escape sequences such as \n are not honoured.
	@Override
	public void printf(String format, Object... args) {
		println(String.format(format, args));
	}

	// Draw the text area at the specifed point, with the specifed width.
	@Override
	public void draw(Graphics2D g, int drawX, int drawY) {
		Color originalColour = g.getColor();
		Font originalFont = g.getFont();

		// Draw the background
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(drawX, drawY, width, height);

		g.setColor(Color.white);
		g.setFont(new Font("MONOSPACED", Font.PLAIN, 12));
		// graphics2D.drawString(ii + "", 100, 100);

		// draw each line
		for (int i = 0, j = head; i < numberOfLinesToShow; i++, j = (j + 1) % lines.length) {
			if (lines[j] == null) return;
			g.drawString(lines[j], drawX + 5, drawY + 12 + (17 * i));
		}

		g.setColor(originalColour);
		g.setFont(originalFont);
	}

	// Scroll backwards by one line
	// Note: since the list is circular the list will wrap around if the start is reached.
	// Note: the text area is scrolled to the end on reciving new input.
	@Override
	public void scrollUp() {
		head = (head - 1 + lines.length) % lines.length;
	}

	// Scroll forwards by one line
	// Note: since the list is circular the list will wrap around if the end is reached.
	// Note: the text area is scrolled to the end on reciving new input.
	@Override
	public void scrollDown() {
		head = (head + 1) % lines.length;
	}

	// Scroll backwards by one page
	// Note: since the list is circular the list will wrap around if the end is reached.
	// Note: the text area is scrolled to the end on reciving new input.
	@Override
	public void pageUp() {
		head = (head - numberOfLinesToShow + lines.length) % lines.length;
	}

	// Note: since the list is circular the list will wrap around if the end is reached.
	// Note: the text area is scrolled to the end on reciving new input.
	@Override
	public void pageDown() {
		head = (head + numberOfLinesToShow) % lines.length;
	}

	// Return the height in xel of the text area
	@Override
	public int getHeight() {
		return height;
	}

	/** @category Generated */
	@Override
	public int getWidth() {
		return width;
	}

	/** @category Generated */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

}
