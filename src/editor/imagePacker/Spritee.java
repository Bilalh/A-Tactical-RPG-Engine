package editor.imagePacker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import common.spritesheet.Sprite;

import config.IPreference;


/**
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("sprite")
public class Spritee extends Sprite implements Comparable<Spritee> {
	
	@XStreamOmitField
	private BufferedImage image;
	
	public Spritee(File file) throws IOException {
		this(file.getName(), ImageIO.read(file));
	}
	
	public Spritee(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	
	/**
	 * Check if this sprite location contains the given x,y position
	 */
	public boolean contains(int xp, int yp) {
		if (xp < x) {
			return false;
		}
		if (yp < y) {
			return false;
		}
		if (xp >= x+width) {
			return false;
		}
		if (yp >= y+height) {
			return false;
		}
		
		return true;
	}


	@Override
	public int compareTo(Spritee b) {
		return this.getHeight() - b.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}


	public void setPositionInSheet(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
}