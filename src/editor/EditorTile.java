package editor;

import common.enums.ImageType;
import common.gui.ResourceManager;
import editor.spritesheet.ISpriteChangedListener;
import editor.spritesheet.MutableSprite;

import view.map.GuiTile;

/**
 * @author Bilal Hussain
 */
public class EditorTile extends GuiTile implements ISpriteChangedListener {

	private MutableSprite sprite;
	
	/** @category Generated */
	public EditorTile(Orientation orientation, float startHeight, float endHeight, 
			int x, int y, MutableSprite sprite, ImageType type) {
		super(orientation, startHeight, endHeight, x, y, sprite.getName(), type);
		sprite.addSpriteChangedListener(this);
	}

	@Override
	public void notifyChanged(MutableSprite newValue) {
		tileImage = ResourceManager.instance().getTile(newValue.getName());
	}
	
}
