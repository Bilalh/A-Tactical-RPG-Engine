package config.xml;

import java.util.HashMap;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
public interface ITileMapping extends IPreference {

	TileImageData getTileImageData(String type);

	String getSpriteSheetLocation();

	HashMap<String, TileImageData> getTilemapping();	
	
}