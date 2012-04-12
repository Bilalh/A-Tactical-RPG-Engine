package common.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * A sprite holds a image.
 * @author Bilal Hussain
 */
public class Sprite {
	
	private BufferedImage image;

	/**
	 * Instantiates a new sprite.
	 * @param image the image to use for the sprite
	 */
	public Sprite(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Draws a the sprite at the specifed location.
	 */
	public void draw(Graphics g,int x,int y) {
		g.drawImage(image,x,y,null);
	}
	
	public int getWidth() {
		return image.getWidth(null);
	}
	
	public int getHeight() {
		return image.getHeight(null);
	}

	public BufferedImage getImage() {
		return image;
	}
	
	
}
