package config.xml;

import common.Location;
import common.interfaces.ILocation;

/**
 * @author Bilal Hussain
 */
public class MapSettings {

	// tileDiagonal * zoom should be whole number 
	// Example value  when using zoom 0,6 0.8 1.0 and 1.2 are
	// 60  80 100
	
	public final int tileDiagonal;
	public final int tileHeight;
	public final float pitch;

	/** @category Generated */
	public MapSettings(int tileDiagonal, int tileHeight, float pitch) {
		this.tileDiagonal = tileDiagonal;
		this.tileHeight = tileHeight;
		this.pitch = pitch;
	}

	public static MapSettings defaults() {
		return new MapSettings(60, 20, .5f);
	}

	@Override
	public String toString() {
		return String.format("MapSettings [tileDiagonal=%s, tileHeight=%s, pitch=%s]", tileDiagonal, tileHeight, pitch);
	}

}
