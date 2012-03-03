package config.xml;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stores locations of resources
 * @author Bilal Hussain
 */
@XStreamAlias("MapData")
public class MapData {

	// TODO add texture fields
	final String tileMappingLocation;
	final String texturesLocation;

	/** @category Generated */
	public MapData(String tileMappingLocation, String texturesLocation) {
		this.tileMappingLocation = tileMappingLocation;
		this.texturesLocation = texturesLocation;
	}

	/** @category Generated */
	public String getTileMappingLocation() {
		return tileMappingLocation;
	}

	/** @category Generated */
	public String getTexturesLocation() {
		return texturesLocation;
	}

}
