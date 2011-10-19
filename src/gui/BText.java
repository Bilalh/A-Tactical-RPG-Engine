package gui;

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


public class BText {

	private int textWidth;
	private int width;
	private int height;
	
	// stores the fonts settings
	private Hashtable<TextAttribute, Object> map =new Hashtable<TextAttribute, Object>();


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
	private int diff;
	private Sprite pic;
	private int hgt;
	private int yOffset;
	
	public void setText(String s) {
		text = new AttributedString(s,map);
		paragraph = text.getIterator();
		paragraphEnd = paragraph.getEndIndex();
		lineMeasurer = null;
	}

	
	public BText(int width, int height, Sprite pic) {
		this.width = width;
		this.diff = 75;
		this.textWidth = width-diff;
		
		yOffset = 25;
		this.height = height - yOffset;
		map.put(TextAttribute.FAMILY, "Serif");
		map.put(TextAttribute.SIZE, new Float(24));
		setText("Many people believe that Vincent van Gogh painted his best works " +
			"during the two-year period he spent in Provence. ");
//			+ "Here is where he " +
//			"painted The Starry Night--which some consider to be his greatest " +
//			"work of all. However, as his artistic brilliance reached new " +
//			"heights in Provence, his physical and mental health plummeted. ");
		
		this.pic = pic;
		
	}

	
	public void nextPage(){
		index = temp;
//		GameEngine.debugConsole().printf("New Page %d", index);
	}

	private Font f = new Font("Serif", Font.PLAIN, 24);
	private FontMetrics metrics;
	private String name = "Rebel Mage";
	
	public void draw(Graphics2D g,  int drawX, int drawY){
		Color originalColour = g.getColor();
		Font originalFont = g.getFont();
		
		g.setColor(new Color(241, 212, 170, 250));
		g.fillRect(drawX, drawY + yOffset, width, height);
		
		// draw the face
		if (pic != null) {
			g.drawImage(pic.getImage(), drawX, drawY, diff, height + yOffset, null);
		}
		
		
		if (lineMeasurer == null){
			FontRenderContext frc = g.getFontRenderContext();
			lineMeasurer = new LineBreakMeasurer(paragraph, frc);
			 metrics = g.getFontMetrics(f);
		    // get the height of a line of text in this font and render context
		    hgt = metrics.getHeight();
		}
		
		// draw name
		
		g.setFont(f);
	    int length = metrics.stringWidth(name);
	    g.setColor(new Color(185,186,113));
	    g.fillRect(drawX + diff, drawY, length+10, yOffset);
	    g.setColor(new Color(0,0,0));
		g.drawString(name, drawX + diff + 5 , drawY + hgt );

		
		// index of the first character in the paragraph.
		int paragraphStart = index + paragraph.getBeginIndex();

		float drawPosX = drawX + diff;
		float drawPosY = drawY + yOffset;
		final float breakWidth = textWidth -10;
		lineMeasurer.setPosition(paragraphStart);

		
		// Get lines until the entire paragraph has been displayed.
		while (lineMeasurer.getPosition() < paragraphEnd) {
			System.out.printf("%s %s\n", drawPosY, height+yOffset);
			// Retrieve next layout. A cleverer program would also cache these layouts until the component is re-sized.
			TextLayout layout = lineMeasurer.nextLayout(breakWidth);

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(g, drawPosX, drawPosY);

			
			if (drawPosY > height+yOffset+ drawY ){
				temp = lineMeasurer.getPosition();
				break;
			}
			
			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		
		g.setColor(originalColour);
		g.setFont(originalFont);
	}





	public int getWidth() {
		return textWidth;
	}


	public int getHeight() {
		return height;
	}
	
	
}
