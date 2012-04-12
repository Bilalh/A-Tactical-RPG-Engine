package common.spritesheet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import config.IPreference;

/**
 * SpriteInfo stores the loaction of a sprite in a spritesheet. 
 * @author Bilal Hussain
 */
@XStreamAlias("sprite")
public class SpriteInfo implements IPreference {

	@XStreamAsAttribute
	protected String name;
	@XStreamAsAttribute
	protected int x;
	@XStreamAsAttribute
	protected int y;
	@XStreamAsAttribute
	protected int width;
	@XStreamAsAttribute
	protected int height;


	public String getName() {
		return name;
	}


	public int getX() {
		return x;
	}


	public int getY() {
		return y;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format("Sprite [name=%s, x=%s, y=%s, width=%s, height=%s]", name, x, y, width, height);
	}

}