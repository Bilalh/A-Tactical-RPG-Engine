package config.xml;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("MapData")
public class MapData {
	
	final String tileMappingLocation;
	
	/** @category Generated */
	public MapData(String tileMappingLocation) {
		this.tileMappingLocation = tileMappingLocation;
	}

	/** @category Generated */
	public String getTileMappingLocation() {
		return tileMappingLocation;
	}

	
}
