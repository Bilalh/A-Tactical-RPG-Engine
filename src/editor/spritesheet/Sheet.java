package editor.spritesheet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sheet implements ISpriteSheet {
	private BufferedImage sheet;
	private ArrayList<MutableSprite> sprites;

	public Sheet(BufferedImage sheet, ArrayList<MutableSprite> sprites) {
		this.sheet = sheet;
		this.sprites = sprites;
	}

	/** @category Generated */
	public BufferedImage getSheetImage() {
		return sheet;
	}

	/** @category Generated */
	ArrayList<MutableSprite> getSprites() {
		return sprites;
	}

}
