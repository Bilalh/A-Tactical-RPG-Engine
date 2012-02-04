package editor;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import common.gui.ResourceManager;
import common.spritesheet.SpriteInfo;
import common.spritesheet.SpriteSheet;
import editor.spritesheet.ISpriteSheet;
import editor.spritesheet.MutableSprite;

/**
 * @author Bilal Hussain
 */
public class EditorSpriteSheet implements ISpriteSheet {

	HashMap<String, MutableSprite> sprites = new HashMap<String, MutableSprite>();
	SpriteSheet spriteSheet;


	public EditorSpriteSheet(SpriteSheet spriteSheet) {
		updateSprites(spriteSheet);
	}

	public void updateSprites(){
		updateSprites(this.spriteSheet);
	}
	
	public void updateSprites(SpriteSheet spriteSheet){
		
		sprites.clear();
		for (Entry<String, BufferedImage> e : spriteSheet.getSpritesMap().entrySet()) {
			sprites.put(e.getKey(), new MutableSprite(e.getKey(), e.getValue()));
		}
		if (this.spriteSheet != spriteSheet){
			this.spriteSheet = spriteSheet;
			ResourceManager.instance().loadSpriteSheet(spriteSheet);	
		}
	}

	public MutableSprite getSprite(String ref){
		return sprites.get(ref);
	}

	public MutableSprite getSpriteAt(int x, int y) {
		for (MutableSprite b : sprites.values()) {
			if (b.contains(x, y)) return b;
		}
		return null;
	}

	public BufferedImage getSheetImage() {
		return spriteSheet.getSheetImage();
	}
}
