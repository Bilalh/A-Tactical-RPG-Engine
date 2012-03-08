package config.xml;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stores locations of resources
 * @author Bilal Hussain
 */
@XStreamAlias("MapData")
public class MapData {

	final String name;
	final String tileMappingLocation;
	final String texturesLocation;
	final String enemiesLocation;
	final String eventsLocation;
	final String musicLocation;
	final String conditionsLocation;

	public MapData(String name, String tileMappingLocation, String texturesLocation, String enemiesLocation, 
			String eventsLocation, String musicLocation, String conditionsLocation) {
		this.name                = name;
		this.tileMappingLocation = tileMappingLocation;
		this.texturesLocation    = texturesLocation;
		this.enemiesLocation     = enemiesLocation;
		this.eventsLocation      = eventsLocation;
		this.musicLocation       = musicLocation;
		this.conditionsLocation  = conditionsLocation;
	}

	public MapData changeName(String s){
		return new MapData(s, tileMappingLocation, texturesLocation, enemiesLocation,eventsLocation,musicLocation,conditionsLocation);
	}
	
	public MapData changeTileMappingLocation(String s){
		return new MapData(name, s, texturesLocation, enemiesLocation,eventsLocation,musicLocation,conditionsLocation);
	}
	
	public MapData changeTexturesLocation(String s){
		return new MapData(name, texturesLocation, s, enemiesLocation,eventsLocation,musicLocation,conditionsLocation);
	}
	
	public MapData changeEnemiesLocation(String s){
		return new MapData(name, texturesLocation, texturesLocation, s,eventsLocation,musicLocation,conditionsLocation);
	}
	
	/** @category Generated */
	public String getTileMappingLocation() {
		return tileMappingLocation;
	}

	/** @category Generated */
	public String getTexturesLocation() {
		return texturesLocation;
	}

	/** @category Generated */
	public String getName() {
		assert name != null;
		return name;
	}

	/** @category Generated */
	public String getEnemiesLocation() {
		return enemiesLocation;
	}

	/** @category Generated */
	public String getEventsLocation() {
		return eventsLocation;
	}

	/** @category Generated */
	public String getMusicLocation() {
		return musicLocation;
	}

	/** @category Generated */
	public String getConditionsLocation() {
		return conditionsLocation;
	}

}
