package common.spritesheet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("sprite")
public class Sprite implements IPreference {

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
	
	/** @category Generated */
	public String getName() {
		return name;
	}
	/** @category Generated */
	public int getX() {
		return x;
	}
	/** @category Generated */
	public int getY() {
		return y;
	}
	/** @category Generated */
	public int getWidth() {
		return width;
	}
	/** @category Generated */
	public int getHeight() {
		return height;
	}

	

}