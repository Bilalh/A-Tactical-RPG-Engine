package common.spritesheet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("sprites")
public class Sprites implements IPreference {

	@XStreamImplicit
	SpriteInfo[] sprites;

	@XStreamAsAttribute
	int width;

	@XStreamAsAttribute
	int height;


	public Sprites(SpriteInfo[] sprites, int width, int height) {
		this.sprites = sprites;
		this.width = width;
		this.height = height;
	}


	public SpriteInfo[] getSprites() {
		return sprites;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}

}
