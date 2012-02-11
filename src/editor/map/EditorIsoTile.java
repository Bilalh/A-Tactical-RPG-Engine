package editor.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import common.ListenerUtil;
import common.enums.ImageType;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.gui.Sprite;
import editor.spritesheet.ISpriteChangedListener;
import editor.spritesheet.MutableSprite;

import view.map.IsoTile;

import static common.ListenerUtil.*;

/**
 * @author Bilal Hussain
 */
public class EditorIsoTile extends IsoTile implements ISpriteChangedListener {

	private MutableSprite sprite;
	private ArrayList<ITileChangedListener> listeners = new ArrayList<ITileChangedListener>();

	static {
		TileState.SELECTED.setColor(Color.BLUE.brighter());
	}

	@Override
	public void draw(int x, int y, Graphics g, boolean drawLeftSide, boolean drawRightSide) {
		if (orientation ==Orientation.EMPTY){
			drawEastWest(x, y, g, drawLeftSide, drawRightSide, true,isSelected());
		}else{
			super.draw(x, y, g, drawLeftSide, drawRightSide);
		}
	}
	
	/** @category Generated */
	public EditorIsoTile(Orientation orientation, float startHeight, float endHeight,
			int x, int y, MutableSprite sprite, ImageType type) {
		super(orientation, startHeight, endHeight, x, y, sprite.getName(), type);
		sprite.addSpriteChangedListener(this);
		this.sprite = sprite;
	}

	public void setSprite(MutableSprite sprite) {
		this.sprite = sprite;
		tileImage = ResourceManager.instance().getTile(sprite.getName());
		notifyListeners(listeners, this);
	}

	public void setHeight(int height) {
		assert height >= 0;
		this.endHeight = this.startHeight = this.height = height;
		notifyListeners(listeners, this);
	}

	public void addTileChangedListener(ITileChangedListener listener) {
		listeners.add(listener);
	}


	public String toFormatedString() {
		return String.format("Tile(%s,%s)[height=%s]", fieldLocation.x, fieldLocation.y, height, type);
	}
	
	@Override
	public void notifyChanged(MutableSprite newValue) {
		setSprite(sprite);
	}

	/** @category Generated */
	public MutableSprite getSprite() {
		return sprite;
	}


	/** @category Generated */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldLocation == null) ? 0 : fieldLocation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof EditorIsoTile)) return false;
		IsoTile other = (EditorIsoTile) obj;
		if (fieldLocation == null) {
			if (other.getFieldLocation() != null) return false;
		} else if (!fieldLocation.equals(other.getFieldLocation())) return false;
		return true;
	}

}
