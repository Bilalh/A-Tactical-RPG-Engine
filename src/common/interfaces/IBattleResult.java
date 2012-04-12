package common.interfaces;


/**
 * Stores the the result of battle.
 * @author Bilal Hussain
 */
public interface IBattleResult {

	IMapUnit getTarget();

	int getDamage();

	boolean isTargetDead();

}