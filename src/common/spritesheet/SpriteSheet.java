package common.spritesheet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;


import config.XMLUtil;

/**
 * @author Bilal Hussain
 */
public class SpriteSheet {

	private BufferedImage sheet;
	private HashMap<String, BufferedImage> sprites;
	
	public SpriteSheet(BufferedImage sheet, InputStream xmldef){
		this.sheet = sheet;
		this.sprites = new HashMap<String, BufferedImage>();
		
		Sprite[] arr = XMLUtil.convertXml(xmldef);
		load(arr);
	}
	
	private void load(Sprite[] arr){
		for (Sprite s : arr) {
			sprites.put(s.name, sheet.getSubimage(s.getX(),s.getY(),s.getWidth(),s.getHeight()));
		}
	}
	
	public BufferedImage getSprite(String ref){
		return sprites.get(ref);
	}
	
	public Map<String, BufferedImage> getSpritesMap(){
		return sprites;
	}

	@Override
	public String toString() {
		final int maxLen = 3;
		return String.format("SpriteSheet [sheet=%s, sprites=%s]", sheet, sprites != null ? toString(sprites.entrySet(), maxLen) : null);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}
