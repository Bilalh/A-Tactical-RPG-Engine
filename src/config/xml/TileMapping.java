package config.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * Maps from the tile type to the image.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("tilemapping")
public class TileMapping implements ITileMapping {

	@XStreamAsAttribute
	final private String spriteSheetLocation;
	@XStreamImplicit
	final private HashMap<String, TileImageData> tilemapping;

	/** @category Generated */
	public TileMapping(String spriteSheet, HashMap<String, TileImageData> tilemapping) {
		this.spriteSheetLocation = spriteSheet;
		this.tilemapping = tilemapping;
	}

	/** @category Generated */
	@Override
	public TileImageData getTileImageData(String type) {
		return tilemapping.get(type);
	}

	/** @category Generated */
	@Override
	public String getSpriteSheetLocation() {
		return spriteSheetLocation;
	}

	/** @category Generated */
	@Override
	public HashMap<String, TileImageData> getTilemapping() {
		return tilemapping;
	}

}
