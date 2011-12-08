package view.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Hashtable;

import common.gui.Sprite;


public class Dialog {

	private int textWidth;
	private int width;
	private int height;
	
	private String name;
	
	// stores the fonts settings
	private Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();


	// The LineBreakMeasurer used to line-break the paragraph.
	private LineBreakMeasurer lineMeasurer;
	private AttributedString text;
	
	// The start postion
	private int index;
	// The poistion of the next char that will not fit
	private int temp;

	
	// index of the first character after the end of the paragraph.
	private int paragraphEnd;
	
	private AttributedCharacterIterator paragraph;
	
	// the diff btween width and textWidth 
	private int xdiff;
	private int yOffset;
	private int lineHeight;
	
	private Sprite pic;
	
	private Font f = new Font("Serif", Font.PLAIN, 18);
	private FontMetrics metrics;
	
	public Dialog(int width, int height){
		this(width, height, null, null);
	}
	
	/** @category Constructor  **/
	public Dialog(int width, int height, String name, Sprite pic) {
		this.width = width;
		this.pic = pic;
		this.xdiff =  pic != null ? 75 : 5;
		this.textWidth = width-xdiff;
		this.name = name;

		yOffset = 25;
		this.height = height - yOffset;
		map.put(TextAttribute.FAMILY, "Serif");
		map.put(TextAttribute.SIZE, new Float(18));
		// TODO testing test.
		setText("Many people believe that Vincent van Gogh painted his best works " +
			"during the two-year period he spent in Provence. Here is where he " +
			"painted The Starry Night--which some consider to be his greatest " +
			"work of all. However, as his artistic brilliance reached new " +
			"heights in Provence, his ysical and mental health plummeted. ");
		
		
	}

	// Returns false if there is no more text 
	public boolean nextPage(){
		
		if (index == temp) return false;
		index = temp;
//		Gui.console().printf("index:%s text:%s", index, paragraphEnd);
		return (index < paragraphEnd);
	}

	/** @category Setter */
	public void setText(String s) {
		text         = new AttributedString(s,map);
		paragraph    = text.getIterator();
		paragraphEnd = paragraph.getEndIndex();
		lineMeasurer = null;
	}
	
	/** @category Setter */
	public void setPicture(Sprite pic) {
		this.pic   = pic;
		this.xdiff = pic != null ? 75 : 5;
	}
	
	/** @category Setter */
	public void setHeight(int height){
		this.height = height - yOffset;
	}
	
	public void draw(Graphics2D g,  int drawX, int drawY){
		Color originalColour = g.getColor();
		Font originalFont = g.getFont();
		
		g.setColor(new Color(241, 212, 170, 250));
		g.fillRect(drawX, drawY + yOffset, width, height);
		
		// draw the face
		if (pic != null) {
			g.drawImage(pic.getImage(), drawX, drawY, xdiff, height + yOffset, null);
		}
		
		
		if (lineMeasurer == null){
			FontRenderContext frc = g.getFontRenderContext();
			lineMeasurer = new LineBreakMeasurer(paragraph, frc);
			 metrics = g.getFontMetrics(f);
		    // get the height of a line of text in this font and render context
		    lineHeight = metrics.getHeight();
		}
		
		g.setFont(f);
	    
		// draw name
		if (name != null){
		    g.setColor(new Color(185,186,113));
		    int length = metrics.stringWidth(name);
		    g.fillRect(drawX + xdiff, drawY, length+10, yOffset);
		    g.setColor(new Color(0,0,0));
			g.drawString(name, drawX + xdiff + 5 , drawY + lineHeight );	
		}
	    g.setColor(new Color(0,0,0));
		
		// index of the first character in the paragraph.
		int paragraphStart = index + paragraph.getBeginIndex();

		float drawPosX = drawX + xdiff;
		float drawPosY = drawY + yOffset;
		final float breakWidth = textWidth -10;
		lineMeasurer.setPosition(paragraphStart);

		// Get lines until the entire paragraph has been displayed.
		while (lineMeasurer.getPosition() < paragraphEnd) {
			// Retrieve next layout. A cleverer program would also cache these layouts until the component is re-sized.
//			System.out.println(drawPosY+  " " + height+ " " + yOffset+ " " + drawY + " " + (height + drawY) );
			TextLayout layout = lineMeasurer.nextLayout(breakWidth);

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(g, drawPosX, drawPosY);

			
			if (drawPosY > height+ drawY ){
				temp = lineMeasurer.getPosition();
				break;
			}
			
			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		
		g.setColor(originalColour);
		g.setFont(originalFont);
	}

	/** @category Getter */
	public int getHeight() {
		return height + yOffset;
	}
	
	/** @category Generated */
	public int getWidth() {
		return textWidth;
	}

	/** @category Generated */
	public String getName() {
		return name;
	}

	/** @category Generated */
	public void setName(String name) {
		this.name = name;
	}

	/** @category Generated */
	public Sprite getPic() {
		return pic;
	}
	
}
