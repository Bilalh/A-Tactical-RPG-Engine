package config.xml;

import java.util.ArrayList;
import java.util.UUID;

import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.Location;
import common.interfaces.Identifiable;
import config.Config;
import config.IPreference;
import config.XMLUtil;

import engine.map.DefaultTurnComparator;
import engine.map.interfaces.ITurnComparator;
import engine.map.win.DefeatAllUnitsCondition;
import engine.map.win.IWinCondition;

/**
 * Stores all the conditions of the map.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("mapConditions")
public class MapConditions implements Identifiable, IPreference {

	private UUID uuid;
	private IWinCondition winCondition;
	private ArrayList<Location> vaildStartLocations;
	private Location defaultStartLocation;
	private ITurnComparator turnComparator;
	
	public MapConditions() {
		uuid = UUID.randomUUID();
	}

	private Object readResolve() {
		if (defaultStartLocation == null){
			defaultStartLocation = new Location(0,0);
		}
		if (turnComparator == null){ 
			turnComparator = new DefaultTurnComparator();
		}
		
		return this;
	}
	

	public IWinCondition getWinCondition() {
		return winCondition;
	}


	public void setWinCondition(IWinCondition winCondition) {
		this.winCondition = winCondition;
	}


	public ArrayList<Location> getVaildStartLocations() {
		return vaildStartLocations;
	}


	public void setVaildStartLocations(ArrayList<Location> vaildStartLocations) {
		this.vaildStartLocations = vaildStartLocations;
	}


	public Location getDefaultStartLocation() {
		return defaultStartLocation;
	}


	public void setDefaultStartLocation(Location defaultStartLocation) {
		this.defaultStartLocation = defaultStartLocation;
	}


	@Override
	public UUID getUuid() {
		return uuid;
	}


	public ITurnComparator getTurnComparator() {
		return turnComparator;
	}


	public void setTurnComparator(ITurnComparator turnComparator) {
		this.turnComparator = turnComparator;
	}
}
