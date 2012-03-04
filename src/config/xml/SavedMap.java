package config.xml;

import java.util.Arrays;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import common.interfaces.Identifiable;

import config.IPreference;
import engine.map.Tile;

/**
 * An Xml representation of an map.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("map")
public class SavedMap implements Identifiable, IPreference {

	@XStreamAsAttribute
	final UUID uuid;
	
	@XStreamAsAttribute
	final int width;
	@XStreamAsAttribute
	final int height;

	MapSettings mapSettings;
	final MapData mapData;

	@XStreamImplicit
	final SavedTile[] tiles;

	public SavedMap(int width, int height, SavedTile[] tiles, MapSettings mapSettings, MapData mapData) {
		this.uuid    = UUID.randomUUID();
		this.width   = width;
		this.height  = height;
		this.tiles   = tiles;
		this.mapData = mapData;
		this.mapSettings = mapSettings;
	}

	// to give default values
	private Object readResolve() {
		if (mapSettings == null){
			mapSettings  = MapSettings.defaults();
		}
		return this;
	}
	
	/** @category Generated */
	public int getFieldWidth() {
		return width;
	}

	/** @category Generated */
	public int getFieldHeight() {
		return height;
	}

	/** @category Generated */
	public MapSettings getMapSettings() {
		return mapSettings;
	}

	/** @category Generated */
	public MapData getMapData() {
		return mapData;
	}

	/** @category Generated */
	public SavedTile[] getTiles() {
		return tiles;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

}
