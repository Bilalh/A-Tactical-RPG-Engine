package editor.spritesheet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import common.spritesheet.SpriteInfo;

import config.IPreference;


/**
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("sprite")
public class Spritee extends SpriteInfo implements Comparable<Spritee> {
	
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
	
	public boolean contains(int xp, int yp) {
		return !(xp < x || yp < y || xp >= x+width || yp >= y+height);
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