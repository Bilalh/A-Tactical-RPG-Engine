package editor.spritesheet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import common.interfaces.ISpriteSheet;

public class Sheet implements ISpriteSheet {
	private BufferedImage sheet;
	private ArrayList<MutableSprite> sprites;

	public Sheet(BufferedImage sheet, ArrayList<MutableSprite> sprites) {
		this.sheet = sheet;
		this.sprites = sprites;
	}

	/** @category Generated */
	@Override
	public BufferedImage getSheetImage() {
		return sheet;
	}

	/** @category Generated */
	ArrayList<MutableSprite> getSprites() {
		return sprites;
	}

	public MutableSprite getSpriteAt(int x, int y) {
		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i).contains(x, y)) {
				return sprites.get(i);
			}
		}
		return null;
	}
	
}
