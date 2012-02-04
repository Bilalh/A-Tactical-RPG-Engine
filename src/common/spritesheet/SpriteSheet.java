package common.spritesheet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;


import config.XMLUtil;
import editor.spritesheet.ISpriteSheet;

/**
 *  Spritesheet contains a number of smaller images, along with a xml file specify the postions
 * @author Bilal Hussain
 */
public class SpriteSheet implements ISpriteSheet {

	protected BufferedImage sheet;
	protected HashMap<String, BufferedImage> sprites;
	
	public SpriteSheet(BufferedImage sheet, InputStream xmldef){
		this.sheet = sheet;
		this.sprites = new HashMap<String, BufferedImage>();
		
		SpriteInfo[] arr = XMLUtil.convertXml(xmldef);
		load(arr);
	}
	
	protected void load(SpriteInfo[] arr){
		for (SpriteInfo s : arr) {
			sprites.put(s.name, sheet.getSubimage(s.getX(),s.getY(),s.getWidth(),s.getHeight()));
		}
	}
	
	public BufferedImage getSpriteImage(String ref){
		return sprites.get(ref);
	}
	
	public Map<String, BufferedImage> getSpritesMap(){
		return sprites;
	}

	/** @category Generated */
	public BufferedImage getSheetImage() {
		return sheet;
	}
	
}
