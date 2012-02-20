package common;

import common.interfaces.IMapUnit;

/**
 * Peforms a battle between two units
 * @author Bilal Hussain
 */
public interface IBattleInfo {

	IMapUnit getAttacker();

	IMapUnit getTarget();

	int getDamage();

	boolean isTargetAlive();

}