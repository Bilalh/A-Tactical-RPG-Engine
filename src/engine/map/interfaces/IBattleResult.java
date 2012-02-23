package engine.map.interfaces;

import common.interfaces.IMapUnit;

/**
 * @author Bilal Hussain
 */
public interface IBattleResult {

	/** @category Generated */
	IMapUnit getTarget();

	/** @category Generated */
	int getDamage();

	/** @category Generated */
	boolean isTargetDead();

}