package editor.imagePacker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sheet {
	private BufferedImage image;
	private ArrayList sprites;
	
	public Sheet(BufferedImage image, ArrayList sprites) {
		this.image = image;
		this.sprites = sprites;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public ArrayList getSprites() {
		return sprites;
	}
}
