package engine.map;

import java.util.Observable;

import common.interfaces.ILocation;

import config.Config;
import config.assets.UnitPlacement;
import config.xml.*;
import engine.map.win.IWinCondition;

/**
 * Map data that is used in the view and the editor.
 * Contains methods to load a map. 
 *  
 * @author Bilal Hussain
 */
public class BasicMap extends Observable {

	protected Tile[][] field;
	protected int width;
	protected int height;

	protected MapData data;
	protected MapSettings mapSettings;
	
	protected ITileMapping tileMapping;
	protected UnitPlacement enemies;
	protected MapEvents events;
	protected MapMusic music;
	protected MapConditions conditions;
	
	/**
	 * Loads a map from resources
	 */
	public void loadMap(String name) {
		SavedMap smap = Config.loadPreference(name);
		assert smap != null;
		
		width = smap.getFieldWidth();
		height = smap.getFieldHeight();
		field = new Tile[width][height];

		for (SavedTile t : smap.getTiles()) {
			field[t.getX()][t.getY()] = new Tile(t);
			assert t.isImpassable() == field[t.getX()][t.getY()].isImpassable();
		}
		
		mapSettings = smap.getMapSettings();
		data        = smap.getMapData();

		assert mapSettings != null;
		assert data        != null;

		// Written like this to catch errors easily
		
		String mappingLocation = data.getTileMappingLocation();
		assert mappingLocation != null;
		
		tileMapping = Config.loadPreference(mappingLocation);
		assert tileMapping != null;
		
		String placement = data.getEnemiesLocation();
		assert placement != null;
		
		enemies = Config.loadPreference(placement);
		assert enemies != null;
		
		String eventsLocation = data.getEventsLocation();
		assert eventsLocation != null;
		
		events = Config.loadPreference(eventsLocation);
		assert events != null;
		
		String musicLocation = data.getMusicLocation();
		assert musicLocation != null;
		
		music = Config.loadPreference(musicLocation);
		assert music != null;
		
		String conditionsLocation = data.getConditionsLocation();
		assert conditionsLocation != null;
		
		conditions = Config.loadPreference(conditionsLocation);
		assert conditions!= null;
		
	}

	public TileImageData getTileImageData(int x, int y) {
		return tileMapping.getTileImageData(field[x][y].getType());
	}

	public String getTileSheetLocation() {
		assert tileMapping.getSpriteSheetLocation() != null;
		assert data.getTexturesLocation() != tileMapping.getSpriteSheetLocation();
		
		return tileMapping.getSpriteSheetLocation();
	}

	public String getTexturesLocation() {
		assert data.getTexturesLocation() != null;
		assert data.getTexturesLocation() != tileMapping.getSpriteSheetLocation();
		
		return data.getTexturesLocation();
	}
	
	public Tile getTile(ILocation l){
		return field[l.getX()][l.getY()];
	}
	
	public Tile getTile(int x, int y){
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

	/** @category Generated */
	public MapMusic getMusic() {
		return music;
	}

	/** @category Generated */
	public MapConditions getConditions() {
		return conditions;
	}


}