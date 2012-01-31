package common.spritesheet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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
		System.out.println(Arrays.toString(arr));
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
	
}
