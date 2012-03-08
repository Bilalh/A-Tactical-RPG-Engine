package engine.map.win;

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
	
	
}
