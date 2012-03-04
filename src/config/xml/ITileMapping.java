package config.xml;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("tilemapping")
public interface ITileMapping extends IPreference {

	TileImageData getTileImageData(String type);

	String getSpriteSheetLocation();

	HashMap<String, TileImageData> getTilemapping();	
	
}