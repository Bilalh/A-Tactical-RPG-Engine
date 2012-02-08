package common.gui;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import view.map.MapSettings;

/**
 * Keeps track of loaded sprites. The sprites are only loaded once.
 * 
 * @author bilalh
 */
public class ResourceManager {
	private static final Logger log = Logger.getLogger(ResourceManager.class);
	private static ResourceManager singleton = new ResourceManager();

	private Map<String, Sprite> sprites = Collections.synchronizedMap(new HashMap<String, Sprite>());
	private static SpriteSheet currentTileSheet;
	
	public synchronized void loadSpriteSheetFromResources(String filepath){
		assert filepath != null;
		currentTileSheet = Config.loadSpriteSheet(filepath);
	}
	
	public  synchronized void loadSpriteSheet(SpriteSheet sheet){
		assert sheet != null;
		currentTileSheet = sheet;
	}
	
	public BufferedImage getTile(String ref){
		assert currentTileSheet != null;
		assert ref != null;
		
		BufferedImage result = currentTileSheet.getSpriteImage(ref);
		assert result != null;
		return result;
	}
	
	public Sprite getSpriteFromClassPath(String path) {
		return getSpriteFromClassPath(path, -1, -1);
	}
	
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
				// TODO catch block in getSprite
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

	public static ResourceManager instance() {
		return singleton;
	}

	/** @category Generated */
	public SpriteSheet getCurrentTileSheet() {
		return currentTileSheet;
	}

}
