package view.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

/**
 * @author Bilal Hussain
 */
public class MenuItem {
	private String name;
	private Pointer arrow;
	
	public MenuItem(String name) {
		this.name = name;
		arrow = new Pointer();
	}
	
	public void draw(Graphics2D g2, boolean selected){
		if (selected){
			g2.translate(-65, -4);
			arrow.draw(g2);
			g2.translate(65, 4);	
		}
		g2.drawString(name, 0, 0);
	}

	
	static class Pointer{
		
		public void draw(Graphics2D g){
			// based off http://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
			
			// to draw a nice curved arrow, fill a V shape rather than stroking it with lines
			// as we're filling rather than stroking, control point is at the apex,

			float arrowRatio = 0.5f;
			float arrowLength = 15.0f;

			BasicStroke stroke = (BasicStroke) g.getStroke();

			float endX = 60.0f;
			float veeX = endX - stroke.getLineWidth() * 0.5f / arrowRatio;

			// vee
			Path2D.Float path = new Path2D.Float();

			float waisting = 0.5f;

			float waistX = endX - arrowLength * 0.5f;
			float waistY = arrowRatio * arrowLength * 0.5f * waisting;
			float arrowWidth = arrowRatio * arrowLength;

			path.moveTo(veeX - arrowLength, -arrowWidth);
			path.quadTo(waistX, -waistY, endX, 0.0f);
			path.quadTo(waistX, waistY, veeX - arrowLength, arrowWidth);

			// end of arrow is pinched in
			path.lineTo(veeX - arrowLength * 0.75f, 0.0f);
			path.lineTo(veeX - arrowLength, -arrowWidth);

			g.setColor(Color.BLACK);
			g.fill(path);
		}
	}


	@Override
	public String toString() {
		return String.format("MenuItem [name=%s]", name);
	}

	
	
}
