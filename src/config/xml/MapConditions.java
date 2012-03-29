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

	public MapConditions() {
		uuid = UUID.randomUUID();
	}

	private Object readResolve() {
		if (defaultStartLocation == null){
			defaultStartLocation = new Location(0,0);
		}
		return this;
	}
	
	/** @category Generated */
	public IWinCondition getWinCondition() {
		return winCondition;
	}

	/** @category Generated */
	public void setWinCondition(IWinCondition winCondition) {
		this.winCondition = winCondition;
	}

	/** @category Generated */
	public ArrayList<Location> getVaildStartLocations() {
		return vaildStartLocations;
	}

	/** @category Generated */
	public void setVaildStartLocations(ArrayList<Location> vaildStartLocations) {
		this.vaildStartLocations = vaildStartLocations;
	}

	/** @category Generated */
	public Location getDefaultStartLocation() {
		return defaultStartLocation;
	}

	/** @category Generated */
	public void setDefaultStartLocation(Location defaultStartLocation) {
		this.defaultStartLocation = defaultStartLocation;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}
}
