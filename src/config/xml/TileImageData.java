package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("tileImageData")
public class TileImageData {

	public static enum Type {
		TEXTURED,
		NON_TEXTURED
	}

	final String location;
	final Type type;

	/** @category Generated Constructor */
	public TileImageData(String location, Type type) {
		this.location = location;
		this.type = type;
	}

	/** @category Generated Getter */
	public String getLocation() {
		return location;
	}

	/** @category Generated Getter */
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("TileImageData [location=%s, type=%s]", location, type);
	}

}
