package common.gui;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 *  Keeps track of loaded sprites.  The sprites are only loaded once.
 * @author bilalh
 */
public class SpriteManager {

	private static SpriteManager singleton = new SpriteManager();
	
	private Map<String, Sprite> sprites = Collections.synchronizedMap(new HashMap<String, Sprite>());

	public Sprite getSprite(String filePath){
		
		// return the sprite if we allready have it.
		if (sprites.containsKey(filePath)){
			return sprites.get(filePath);
		}
		
		BufferedImage sourceImage = null;
		
		URL url = this.getClass().getClassLoader().getResource(filePath);
		if (url == null){
//			System.err.printf("Image load of %s failed", filePath);
			throw new RuntimeException(String.format("Image load of %s failed", filePath));
		}

		try {
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			// TODO catch block in getSprite
			e.printStackTrace();
		}

		
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage  image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		image.getGraphics().drawImage(sourceImage,0,0,null);
		
		Sprite sprite = new Sprite(image);
		sprites.put(filePath,sprite);

		return sprite;
	}
	
	
	private SpriteManager(){}
	
	public static SpriteManager instance(){
		return singleton;
	}
	
}
