package common.spritesheet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

import common.interfaces.ISpriteSheet;

import util.Args;

import config.XMLUtil;

/**
 * Spritesheet contains a number of smaller images, along with a xml file specify the postions.
 * 
 * @author Bilal Hussain
 */
public class SpriteSheet implements ISpriteSheet {

	protected BufferedImage sheet;

	protected HashMap<String, BufferedImage> sprites;
	
	protected int width, height;

	/**
	 * Instantiates a new sprite sheet.
	 * 
	 * @param sheet the sheet
	 * @param xmldef the xml specify the how the sheet is arranged
	 */
	public SpriteSheet(BufferedImage sheet, InputStream xmldef) {
		assert sheet != null;
		assert xmldef != null;

		this.sheet = sheet;
		this.sprites = new HashMap<String, BufferedImage>();

		Sprites ss = XMLUtil.convertXml(xmldef);
		load(ss.getSprites());
		width = ss.getWidth();
		height = ss.getHeight();

		assert width > 0;
		assert height > 0;
	}

	/**
	 * Load a sheets from a array of sprites
	 */
	protected void load(SpriteInfo[] arr) {
		assert arr != null;
		assert arr.length > 0;

		for (SpriteInfo s : arr) {
			assert s != null;
			sprites.put(s.name, sheet.getSubimage(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
		}
	}

	/**
	 * Gets the sprite image.
	 * 
	 * @param ref the ref
	 * @return the sprite image
	 */
	public BufferedImage getSpriteImage(String ref) {
		return sprites.get(ref);
	}

	public Map<String, BufferedImage> getSpritesMap() {
		return sprites;
	}

	@Override
	public BufferedImage getSheetImage() {
		return sheet;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return String.format("SpriteSheet [sprites=%s, width=%s, height=%s]", sprites.keySet(), width, height);
	}

}
