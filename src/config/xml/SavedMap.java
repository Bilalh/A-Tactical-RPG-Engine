package config.xml;

import java.util.Arrays;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;
import engine.map.Tile;

/**
 * An Xml representation of an map.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("map")
public class SavedMap implements IPreference {

	@XStreamAsAttribute
	final int width;
	@XStreamAsAttribute
	final int height;

	final MapSettings mapSettings;
	final MapData mapData;

	@XStreamImplicit
	final SavedTile[] tiles;
	
	/** @category Generated */
	public SavedMap(int width, int height, SavedTile[] tiles, MapSettings mapSettings, MapData mapData) {
		this.width = width;
		this.height = height;
		this.tiles = tiles;
		this.mapSettings = mapSettings;
		this.mapData = mapData;
	}

	/** @category Generated */
	int getWidth() {
		return width;
	}

	/** @category Generated */
	int getHeight() {
		return height;
	}

	/** @category Generated */
	SavedTile[] getTiles() {
		return tiles;
	}

	/** @category Generated */
	MapSettings getMapSettings() {
		return mapSettings;
	}

	/** @category Generated */
	MapData getMapData() {
		return mapData;
	}

}
