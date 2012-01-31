package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.enums.ImageType;

/**
 * Store the location and image type of a tile.
 * @author Bilal Hussain
 */
@XStreamAlias("tileImageData")
public class TileImageData {

	final String location;
	final ImageType type;

	/** @category Generated Constructor */
	public TileImageData(String location, ImageType type) {
		this.location = location;
		this.type = type;
	}

	/** @category Generated Getter */
	public String getLocation() {
		return location;
	}

	/** @category Generated Getter */
	public ImageType getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("TileImageData [location=%s, type=%s]", location, type);
	}

}
