package view.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import view.ui.interfaces.IConsole;

/**
 * A Console displays uneditable text graphically to the user. draw has to be called to draw the console
 * The console has a finite history which can scroll backwards and fowards. The number of lines shown is 
 * specifed at creation and the resulting height of the textarea can be found using getHeight.
 * 
 * @author Bilal Hussain
 */
public class Console implements IConsole {

	/** lines is a circular list */
	private String[] lines;
	/** index is the next free postion, */
	private int index;
	/** head is the index in lines to print from */
	private int head;
	/** Number of lines to show */
	private final int numberOfLinesToShow;
	/** Height in pixels */
	private final int height;
	/** Width in pixels */
	private int width;

	/** To make sure only the last n lines are shown */
	private boolean around;
	/** Lines are numbered if true */
	private final boolean numberedLines;
	/** The current line number */
	private int lineIndex;

	/** If not null the text is printed the stream */
	private PrintStream out;
	
	/** MonoSpace font */
	private Font font = new Font("MONOSPACED", Font.PLAIN, 12);
	private float lineheight = 17f;
	
	
	public Console() {
		this(500, 5, true,null);
	}

	public Console(int history, int linesToShow, boolean numberedLines, PrintStream stream) {
		if (linesToShow <= 0 || linesToShow > history || history <= 0) throw new IllegalArgumentException("Invaild Arguments");
		
		lines  = new String[history];
		index  = head = lineIndex = 0;
		around = false;
		out    = stream;
		height = (int) (lineheight * linesToShow);

		this.numberedLines  = numberedLines;
		numberOfLinesToShow = linesToShow;
	}

	/**
	 * Adds a string to the Console, the string will be printed on a new line
	 * Note: Escape sequences such as \n are not honoured.
	 */
	@Override
	public void println(Object newObj) {
		if (newObj == null) throw new IllegalArgumentException("String can not be null");

		String newString = newObj.toString();
		if (out != null) out.println(newString);
		lines[index] = numberedLines ? (lineIndex++) + " " + newString : newString ;

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

	/**
	 * Adds a string to the Console, the string will be printed on a new line
	 * Note: Escape sequences such as \n are not honoured.
	 */
	@Override
	public void printf(String format, Object... args) {
		println(String.format(format, args));
	}

	/** Draw the console at the specifed point, with the specifed width. */
	@Override
	public void draw(Graphics2D g, int drawX, int drawY) {
		Color old = g.getColor();
		Font oldFont = g.getFont();

		// Draw the background
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(drawX, drawY, width, height);

		g.setColor(Color.white);
		g.setFont(font);

		// draw each line
		for (int i = 0, j = head; i < numberOfLinesToShow; i++, j = (j + 1) % lines.length) {
			if (lines[j] == null) return;
			g.drawString(lines[j], drawX + 5, drawY + 12 + (17 * i));
		}

		g.setColor(old);
		g.setFont(oldFont);
	}

	/**
	 * Scroll backwards by one line
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	@Override
	public void scrollUp() {
		int index = (head - 1 + lines.length) % lines.length;
		if (lines[index] == null) return;
		head = index;
	}

	/**
	 * Scroll forwards by one line
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	@Override
	public void scrollDown() {
		index =  (head + 1) % lines.length;
		if (lines[index] == null) return;
		head = index;
	}

	/**
	 * Scroll backwards by one page
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input. 
	 */
	@Override
	public void pageUp() {
		int index = (head - numberOfLinesToShow + lines.length) % lines.length;
		if (lines[index] == null) return;
		head = (head - numberOfLinesToShow + lines.length) % lines.length;
	}

	/**
	 * Note: since the list is circular the list will wrap around if the start is reached.
	 * Note: the text area is scrolled to the end on reciving new input.
	 */
	@Override
	public void pageDown() {
		int index = (head + numberOfLinesToShow) % lines.length;
		if (lines[index] == null) return;
		head = (head + numberOfLinesToShow) % lines.length;
	}

	/**
	 * Returns the height in pixels of the text area.
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width in pixels of the text area.
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the console
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Set the extra stream to print to
	 */
	@Override
	public void setPrintStream(PrintStream out) {
		this.out = out;
	}

}
