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
	final boolean outlines;
	
	public MapData(String name, String tileMappingLocation, String texturesLocation, String enemiesLocation, 
			String eventsLocation, String musicLocation, String conditionsLocation, boolean outlines) {
		this.name                = name;
		this.tileMappingLocation = tileMappingLocation;
		this.texturesLocation    = texturesLocation;
		this.enemiesLocation     = enemiesLocation;
		this.eventsLocation      = eventsLocation;
		this.musicLocation       = musicLocation;
		this.conditionsLocation  = conditionsLocation;
		this.outlines            = outlines;
	}

	
	public MapData(String name,  String tileMappingLocation, String texturesLocation) {
		this(name,
			tileMappingLocation,
			texturesLocation,
			"maps/" + name+ "-enemies.xml",
			"maps/" + name+ "-events.xml",
			"maps/" + name+ "-music.xml",
			"maps/" + name+ "-conditions.xml",
			false);
	}

	public MapData changeName(String s){
		return new MapData(s, tileMappingLocation, texturesLocation, enemiesLocation,eventsLocation,musicLocation,conditionsLocation,false);
	}
	
	public MapData changeTileMappingLocation(String s){
		return new MapData(name, s, texturesLocation, enemiesLocation,eventsLocation,musicLocation,conditionsLocation,false);
	}
	
	public MapData changeTexturesLocation(String s){
		return new MapData(name, texturesLocation, s, enemiesLocation,eventsLocation,musicLocation,conditionsLocation,false);
	}
	
	public MapData changeEnemiesLocation(String s){
		return new MapData(name, texturesLocation, texturesLocation, s,eventsLocation,musicLocation,conditionsLocation,false);
	}
	
	public MapData changeOutline(boolean b){
		return new MapData(name, texturesLocation, texturesLocation, enemiesLocation, eventsLocation,musicLocation,conditionsLocation,b);
	}
	

	public String getTileMappingLocation() {
		return tileMappingLocation;
	}


	public String getTexturesLocation() {
		return texturesLocation;
	}


	public String getName() {
		assert name != null;
		return name;
	}


	public String getEnemiesLocation() {
		return enemiesLocation;
	}


	public String getEventsLocation() {
		return eventsLocation;
	}


	public String getMusicLocation() {
		return musicLocation;
	}


	public String getConditionsLocation() {
		return conditionsLocation;
	}


	public boolean hasOutlines() {
		return outlines;
	}

}
