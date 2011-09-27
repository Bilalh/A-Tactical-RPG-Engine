package common.gui;

import java.awt.Graphics;
import java.awt.Image;

/**
 * @author bilalh
 */
public class Sprite {

	private Image image;

	/** @category Generated Constructor */
	public Sprite(Image image) {
		this.image = image;
	}
	
	// Draws the sprite at the specifed location
	public void draw(Graphics g,int x,int y) {
		g.drawImage(image,x,y,null);
	}
	
	/** @category Getter */
	public int getWidth() {
		return image.getWidth(null);
	}
	
	/** @category Getter */
	public int getHeight() {
		return image.getHeight(null);
	}
	
	
}