package common.spritesheet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("sprites")
public class Sprites {

	@XStreamImplicit
	SpriteInfo[] sprites;

	@XStreamAsAttribute
	int width;

	@XStreamAsAttribute
	int height;

	/** @category Generated */
	public Sprites(SpriteInfo[] sprites, int width, int height) {
		this.sprites = sprites;
		this.width = width;
		this.height = height;
	}

	/** @category Generated */
	public SpriteInfo[] getSprites() {
		return sprites;
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
