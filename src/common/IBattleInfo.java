package common;

import java.util.Collection;

import common.interfaces.IMapUnit;
import engine.map.BattleResult;

/**
 * Peforms a battle between two units
 * 
 * @author Bilal Hussain
 */
public interface IBattleInfo {

	IMapUnit getAttacker();

	Collection<BattleResult> getResults();

}