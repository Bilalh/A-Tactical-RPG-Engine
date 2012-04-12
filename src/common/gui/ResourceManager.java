package common.gui;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import common.spritesheet.SpriteSheet;
import config.Config;

import util.Args;
import util.Logf;

/**
 * Keeps track of loaded tiles, which only loaded once.
 *
 * @author bilalh
 */
public class ResourceManager {
	private static final Logger log = Logger.getLogger(ResourceManager.class);
	private static ResourceManager singleton = new ResourceManager();

	private Map<String, Sprite> sprites = Collections.synchronizedMap(new HashMap<String, Sprite>());
	private static SpriteSheet currentTileSheet    = null;
	private static SpriteSheet currentItemSheet    = null;
	private static SpriteSheet currentTextureSheet = null;
	
	
	
	/**
	 * Load a tile sheet from the resources.
	 *
	 * @param filepath the filepath
	 */
	public synchronized void loadTileSheetFromResources(String filepath){
		assert filepath != null;
		currentTileSheet = Config.loadSpriteSheet(filepath);
		assert currentTextureSheet != currentTileSheet;
	}
	
	/**
	 * Loads a tile sheet.
	 *
	 * @param sheet the sheet
	 */
	public  synchronized void loadTileSheet(SpriteSheet sheet){
		assert sheet != null;
		currentTileSheet = sheet;
		assert currentTextureSheet != currentTileSheet:  currentTextureSheet + "\n" + currentTileSheet;
	}

	/**
	 * Loads a  item sheet from resources.
	 *
	 * @param filepath the filepath
	 */
	public synchronized void loadItemSheetFromResources(String filepath){
		assert filepath != null;
		currentItemSheet = Config.loadSpriteSheet(filepath);
	}
	
	/**
	 * Load an item sheet.
	 *
	 * @param sheet the sheet to load
	 */
	public  synchronized void loadItemSheet(SpriteSheet sheet){
		assert sheet != null;
		currentItemSheet = sheet;
	}
	
	/**
	 * Load a texture sheet from resources.
	 *
	 * @param filepath the filepath
	 */
	public synchronized void loadTextureSheetFromResources(String filepath){
		if (filepath == null) return;
		currentTextureSheet = Config.loadSpriteSheet(filepath);
		assert currentTextureSheet != currentTileSheet;
	}
	
	/**
	 * Load a texture sheet.
	 *
	 * @param sheet the sheet
	 */
	public  synchronized void loadTextureSheet(SpriteSheet sheet){
		assert sheet != null;
		currentTextureSheet = sheet;
		assert currentTextureSheet != currentTileSheet;
	}
	
	
	/**
	 * Gets the tile with the specifed name.
	 *
	 * @param ref the  the name of the tile.
	 * @return the tile
	 */
	public BufferedImage getTile(String ref){
		assert currentTileSheet != null;
		assert ref != null;
		
		BufferedImage result = currentTileSheet.getSpriteImage(ref);
		assert result != null : ref + " not found";
		return result;
	}

	/**
	 * Gets the item specifed by a name
	 *
	 * @param ref the name of the item
	 * @return the item
	 */
	public BufferedImage getItem(String ref){
		assert currentItemSheet != null;
		assert ref != null;
		
		BufferedImage result = currentItemSheet.getSpriteImage(ref);
		assert result != null;
		return result;
	}
	
	private Map<String, TexturePaint> texturedTiles = Collections.synchronizedMap(new HashMap<String, TexturePaint>());
	
	/**
	 * Gets the textured tile.
	 *
	 * @param ref the ref
	 * @return the textured tile
	 */
	public TexturePaint getTexturedTile(String ref){
		assert ref != null;
		assert currentTextureSheet != null;
		
		if (texturedTiles.containsKey(ref)){
			return texturedTiles.get(ref);
		}
		
		BufferedImage tile = currentTextureSheet.getSpriteImage(ref);
		assert tile != null : ref;
		
		Rectangle2D rTile   = new Rectangle2D.Double(0, 0, tile.getWidth(null),tile.getHeight(null));
		TexturePaint tTile  = new TexturePaint( tile, rTile);
		assert tTile != null;
		
		texturedTiles.put(ref, tTile);
		return tTile;
	}

	private Map<String, BufferedImage> tilesResized = Collections.synchronizedMap(new HashMap<String, BufferedImage>());
	
	/**
	 * Gets the tile, resizing if needed
	 *
	 * @param ref the tile name
	 * @param width the required width
	 * @param height the required height
	 * @return the tile
	 */
	public BufferedImage getTile(String ref, int width, int height){
		String path = ref + width+height;
		if (tilesResized.containsKey(path)){
			return tilesResized.get(path);
		}
//		Logf.info(log, "%s %s %s", path, width,height);
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(width, height, Transparency.BITMASK);
		Graphics g = image.getGraphics();
		g.drawImage(getTile(ref), 0, 0, width,height, null);
		g.dispose();
		tilesResized.put(path,image);
		return image;
	}
	
	/**
	 * Gets a sprite from the classpath.
	 *
	 * @param path the path
	 * @return the sprite
	 */
	public Sprite getSpriteFromClassPath(String path) {
		return getSpriteFromClassPath(path, -1, -1);
	}
	
	
	/**
	 * Gets the sprite from the classpath.
	 *
	 * @param path the path to load from
	 * @param width the requied width
	 * @param height the requied height
	 * @return the sprite.
	 */
	public Sprite getSpriteFromClassPath(String path, int width, int height) {
		assert path != null;
		
		boolean resized =false;
		String check = path;
		if (width > 0){
			resized = true;
			check+=width;
		}
		if (height > 0){
			resized = true;
			check+=height;
		}
				
		if (sprites.containsKey(check)) {
			return sprites.get(check);
		}

		BufferedImage sourceImage = null;
		
		if (sprites.containsKey(path)) {
			sourceImage = sprites.get(path).getImage();
		}else{
			URL url = this.getClass().getClassLoader().getResource(path);
			if (url == null) {
				throw new RuntimeException(String.format("Image load of %s failed", path));
			}

			try {
				sourceImage = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		if (width < 0){
			width   = sourceImage.getWidth();
		}
		if (height < 0){
			height  = sourceImage.getHeight();
		}
		
		BufferedImage image = gc.createCompatibleImage(width, height, Transparency.BITMASK);
//		image.getGraphics().drawImage(sourceImage, 0, 0, null);
		Graphics g = image.getGraphics();
		g.drawImage(sourceImage, 0, 0, width,height, null);
		g.dispose();
		
		Sprite sprite = new Sprite(image);
		sprites.put(check, sprite);

		Logf.info(log, "Loaded %s",check);

		return sprite;
	}

	private ResourceManager() {
	}

	/**
	 * @return the resource manager
	 */
	public static ResourceManager instance() {
		return singleton;
	}

	public SpriteSheet getCurrentTileSheet() {
		return currentTileSheet;
	}
	
	public SpriteSheet getCurrentTextureSheet() {
		return currentTextureSheet;
	}
	
	public SpriteSheet getCurrentItemSheet() {
		return currentItemSheet;
	}

}


