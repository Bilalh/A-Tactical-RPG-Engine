package engine.map;

import java.util.Observable;

import config.Config;
import config.xml.*;

/**
 * Map Shared between the game and the editor.
 * 
 * @author Bilal Hussain
 */
public class BasicMap extends Observable {

	protected Tile[][] field;
	protected int width;
	protected int height;

	protected ITileMapping tileMapping;
	protected MapSettings mapSettings;

	protected MapData data;
	
	public void loadMap(String name) {
		SavedMap smap = Config.loadPreference(name);

		width = smap.getFieldWidth();
		height = smap.getFieldHeight();
		field = new Tile[width][height];

		for (SavedTile t : smap.getTiles()) {
			field[t.getX()][t.getY()] = new Tile(t.getHeight(), (t.getHeight()), t.getType());
		}

		mapSettings = smap.getMapSettings();
		data = smap.getMapData();

		String mappingLocation = data.getTileMappingLocation();
		if (mappingLocation == null) {
			tileMapping = Config.defaultMapping();
		} else {
			tileMapping = Config.loadPreference(mappingLocation);
		}

	}


	public TileImageData getTileImageData(int x, int y) {
		return tileMapping.getTileImageData(field[x][y].getType());
	}

	public String getTileSheetLocation() {
		return tileMapping.getSpriteSheetLocation();
	}

	public Tile getTile(int x, int y) {
		return field[x][y];
	}
	
	/** @category Generated */
	public Tile[][] getField() {
		return field;
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
	public ITileMapping getTileMapping() {
		return tileMapping;
	}

	/** @category Generated */
	public MapSettings getMapSettings() {
		return mapSettings;
	}


	/** @category Generated */
	public MapData getData() {
		return data;
	}

}