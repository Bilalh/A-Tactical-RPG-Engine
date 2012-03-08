package config.xml;

import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import common.interfaces.Identifiable;
import config.Config;
import config.IPreference;

import engine.map.win.DefeatAllUnitsCondition;
import engine.map.win.IWinCondition;

/**
 * Stores all the conditions of the map.
 * @author Bilal Hussain
 */
@XStreamAlias("mapConditions")
public class MapConditions implements Identifiable, IPreference {

	private UUID uuid;
	private IWinCondition winCondition;

	public MapConditions(){
		uuid = UUID.randomUUID();
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
	@Override
	public UUID getUuid() {
		return uuid;
	}
	
}
