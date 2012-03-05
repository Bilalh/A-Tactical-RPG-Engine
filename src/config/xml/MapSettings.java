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
	
	public int tileDiagonal;
	public int tileHeight;
	public float pitch;
    public float zoom;

	/** @category Generated */
	private MapSettings(int tileDiagonal, int tileHeight, float pitch, float zoom) {
		this.tileDiagonal = tileDiagonal;
		this.tileHeight = tileHeight;
		this.pitch = pitch;
		this.zoom = zoom;
	}


	// to give default values
	private Object readResolve() {
		zoom = 1;
		return this;
	}
	
	public static MapSettings defaults() {
		MapSettings s = new MapSettings(60, 10, .5f,1);
		assert s != null;
		return s;
	}

	@Override
	public String toString() {
		return String.format("MapSettings [tileDiagonal=%s, tileHeight=%s, pitch=%s]", tileDiagonal, tileHeight, pitch);
	}

}
