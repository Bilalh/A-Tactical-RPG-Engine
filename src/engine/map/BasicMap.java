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
	
	public BasicMap(){}
	
	public void loadMap(String name) {
		SavedMap smap = Config.loadPreference(name);
		assert smap != null;
		width = smap.getFieldWidth();
		height = smap.getFieldHeight();
		field = new Tile[width][height];

//		float max = 0;
//		for (SavedTile t : smap.getTiles()) {
//			max = Math.max(max, t.getHeight());
//		}

		for (SavedTile t : smap.getTiles()) {
//			float h = ((t.getHeight())/max)*5;
//			field[t.getX()][t.getY()] = new Tile((int)h, (int)h, t.getType());
			field[t.getX()][t.getY()] = new Tile(t.getHeight(),t.getHeight() , t.getType());
		}
		
		
		mapSettings = smap.getMapSettings();
		data = smap.getMapData();

		assert mapSettings != null;
		assert data != null;

		String mappingLocation = data.getTileMappingLocation();

		if (mappingLocation == null) {
			tileMapping = Config.defaultMapping();
		} else {
			tileMapping = Config.loadPreference(mappingLocation);
			assert tileMapping != null;
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