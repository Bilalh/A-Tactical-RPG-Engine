package editor.map;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.xml.ITileMapping;
import config.xml.TileImageData;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("tilemapping")
public class MutableTileMapping implements ITileMapping {

	@XStreamAsAttribute
	private String spriteSheetLocation;
	@XStreamImplicit
	private HashMap<String, TileImageData> tilemapping;

	/** @category Generated */
	public MutableTileMapping(ITileMapping t) {
		this.spriteSheetLocation = t.getSpriteSheetLocation();
		this.tilemapping         = t.getTilemapping();
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
