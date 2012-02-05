package editor;

import java.util.ArrayList;

import common.ListenerUtil;
import common.enums.ImageType;
import common.gui.ResourceManager;
import common.gui.Sprite;
import editor.spritesheet.ISpriteChangedListener;
import editor.spritesheet.MutableSprite;

import view.map.GuiTile;

import static common.ListenerUtil.*;

/**
 * @author Bilal Hussain
 */
public class EditorTile extends GuiTile implements ISpriteChangedListener {

	private MutableSprite sprite;
	private ArrayList<ITileChangedListener> listeners = new ArrayList<ITileChangedListener>();
	
	/** @category Generated */
	public EditorTile(Orientation orientation, float startHeight, float endHeight, 
			int x, int y, MutableSprite sprite, ImageType type) {
		super(orientation, startHeight, endHeight, x, y, sprite.getName(), type);
		sprite.addSpriteChangedListener(this);
	}
	
	public void setSprite(MutableSprite sprite){
		this.sprite = sprite;
		tileImage = ResourceManager.instance().getTile(sprite.getName());
		notifyListeners(listeners, this);
	}
	
	public void setHeight(int height){
		assert height >=0;
		this.endHeight = this.startHeight = this.height = height;
		notifyListeners(listeners, this);
	}
	
	public void addTileChangedListener(ITileChangedListener listener){
		listeners.add(listener);
	}
	
	@Override
	public void notifyChanged(MutableSprite newValue) {
		setSprite(sprite);
	}
	
}
