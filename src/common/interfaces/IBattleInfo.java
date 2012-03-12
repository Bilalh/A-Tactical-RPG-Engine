package common.interfaces;

import java.util.Collection;

import engine.map.BattleResult;

/**
 * Peforms a battle between two units
 * 
 * @author Bilal Hussain
 */
public interface IBattleInfo {

	IMapUnit getAttacker();

	Collection<BattleResult> getResults();

	boolean hasLeveledUp();
	
}