package editor.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import common.ListenerUtil;
import common.enums.ImageType;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.gui.Sprite;
import config.xml.MapSettings;
import editor.spritesheet.ISpriteChangedListener;
import editor.spritesheet.MutableSprite;

import view.map.IsoTile;

import static common.ListenerUtil.*;

/**
 * @author Bilal Hussain
 */
public class EditorIsoTile extends IsoTile implements ISpriteChangedListener {

	private MutableSprite sprite, leftWallSprite, rightWallSprite;

	static {
		TileState.SELECTED.setColor(Color.BLUE.brighter());
	}

	@Override
	public void draw(int x, int y, Graphics g) {
		if (orientation ==Orientation.EMPTY){
			drawEastWest(x, y, g, true, isSelected());
		}else{
			super.draw(x, y, g);
		}
	}
	

	public EditorIsoTile(Orientation orientation, float startHeight, float endHeight,
			int x, int y, MutableSprite sprite, ImageType type,MapSettings settings, String leftWallRef, String rightWallRef) {
		super(orientation, startHeight, endHeight, x, y, sprite.getName(), type,settings, leftWallRef,rightWallRef);
		sprite.addSpriteChangedListener(this);
		this.sprite = sprite;
	}

	public EditorIsoTile(Orientation orientation, float startHeight, float endHeight,int x, int y,MapSettings settings) {
		super(orientation, startHeight, endHeight, x, y, "none", ImageType.NON_TEXTURED,settings,null,null);
	}
	
	public void setSprite(MutableSprite sprite) {
		this.sprite = sprite;
		this.tileName = sprite.getName();
		tileImage = ResourceManager.instance().getTile(sprite.getName());
	}

	public void setLeftWallSprite(MutableSprite sprite){
		leftWallSprite = sprite;
		leftWallName   = sprite.getName();
		leftWall       = ResourceManager.instance().getTexturedTile(leftWallName);
	}

	public void setRightWallSprite(MutableSprite sprite){
		rightWallSprite = sprite;
		rightWallName   = sprite.getName();
		rightWall       = ResourceManager.instance().getTexturedTile(rightWallName);
	}
	
	public void setHeight(int height) {
		assert height >= 0;
		this.endHeight = this.startHeight = this.height = height;
	}

	@Override
	public void notifyChanged(MutableSprite newValue) {
		setSprite(sprite);
	}

	public String toFormatedString() {
		return String.format("Tile(%s,%s)[height=%s]", fieldLocation.x, fieldLocation.y, height, type);
	}
	

	public MutableSprite getSprite() {
		return sprite;
	}

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
		EditorIsoTile other = (EditorIsoTile) obj;
		if (fieldLocation == null) {
			if (other.getLocation() != null) return false;
		} else if (!fieldLocation.equals(other.getLocation())) return false;
		return true;
	}


	public MutableSprite getLeftWallSprite() {
		return leftWallSprite;
	}


	public MutableSprite getRightWallSprite() {
		return rightWallSprite;
	}



	public void setStartingHeight(float startHeight) {
		this.startHeight = startHeight;
	}
	


	public void setTileName(String tileName) {
		this.tileName = tileName;
	}

	public void setEndHeight(int height) {
		this.endHeight = height;
	}
	
}
