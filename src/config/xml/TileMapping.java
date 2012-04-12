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
	final protected String spriteSheetLocation;
	@XStreamImplicit
	final protected HashMap<String, TileImageData> tilemapping;

	public TileMapping(String spriteSheetLocation, HashMap<String, TileImageData> tilemapping) {
		this.spriteSheetLocation = spriteSheetLocation;
		this.tilemapping         = tilemapping;
	}
	
	public TileMapping(ITileMapping t){
		this(t.getSpriteSheetLocation(), t.getTilemapping());
	}


	@Override
	public TileImageData getTileImageData(String type) {
		return tilemapping.get(type);
	}


	@Override
	public String getSpriteSheetLocation() {
		return spriteSheetLocation;
	}


	@Override
	public HashMap<String, TileImageData> getTilemapping() {
		return tilemapping;
	}

}
