package config.xml;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stores locations of resources
 * @author Bilal Hussain
 */
@XStreamAlias("MapData")
public class MapData {

	final String name;
	final String tileMappingLocation;
	final String texturesLocation;

	/** @category Generated */
	public MapData(String tileMappingLocation, String texturesLocation,String name) {
		this.tileMappingLocation = tileMappingLocation;
		this.texturesLocation = texturesLocation;
		this.name = name;
	}

	/** @category Generated */
	public String getTileMappingLocation() {
		return tileMappingLocation;
	}

	/** @category Generated */
	public String getTexturesLocation() {
		return texturesLocation;
	}

	/** @category Generated */
	public String getName() {
		assert name != null;
		return name;
	}

}
