package engine.map.win;

import java.util.Collection;

import common.interfaces.IUnit;

import engine.map.Map;
import engine.map.MapPlayer;

/**
 * The player win if he/she can defeat all the enemies units 
 * @author Bilal Hussain
 */
public class DefeatAllUnitsCondition implements IWinCondition {

	@Override
	public boolean hasWon(Map map, MapPlayer player, MapPlayer ai){
		return ai.getUnits().isEmpty();
	}


	@Override
	public String info(Collection<? extends IUnit> units) {
		return "Defeat All Enemy Units";
	}
	
	
}
