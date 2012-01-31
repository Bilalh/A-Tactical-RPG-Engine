package editor.spritesheet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sheet {
	private BufferedImage image;
	private ArrayList<MutableSprite> sprites;

	public Sheet(BufferedImage image, ArrayList sprites) {
		this.image = image;
		this.sprites = sprites;
	}

	public BufferedImage getImage() {
		return image;
	}

	public ArrayList<MutableSprite> getSprites() {
		return sprites;
	}
}
