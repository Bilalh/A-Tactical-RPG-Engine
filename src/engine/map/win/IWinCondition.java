package engine.map.win;

import java.util.Collection;

import common.interfaces.IUnit;

import engine.map.Map;
import engine.map.MapPlayer;

/**
 *  The winning condition.
 * @author Bilal Hussain
 */
public interface IWinCondition {

	/**
	 * Return true if the player has won
	 */
	boolean hasWon(Map map, MapPlayer player, MapPlayer ai);
	
	String info(Collection<? extends IUnit> units);
	
}