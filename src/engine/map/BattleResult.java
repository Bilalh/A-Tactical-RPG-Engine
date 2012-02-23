package engine.map;

import common.interfaces.IBattleResult;
import common.interfaces.IMapUnit;

import engine.map.interfaces.IMutableMapUnit;

/**
 * Holds the results from a battle.
 * @author Bilal Hussain
 */
public class BattleResult implements IBattleResult {

	private IMutableMapUnit target;
	private final int damage;
	private final boolean dead;

	/** @category Generated */
	public BattleResult(IMutableMapUnit target, int damage, boolean died) {
		this.target = target;
		this.damage = damage;
		this.dead = died;
	}

	/** @category Generated */
	@Override
	public IMapUnit getTarget() {
		return target;
	}

	IMutableMapUnit getMutableTarget(){
		return target;
	}
	
	/** @category Generated */
	@Override
	public int getDamage() {
		return damage;
	}

	/** @category Generated */
	@Override
	public boolean isTargetDead() {
		return dead;
	}

	@Override
	public String toString() {
		return String.format("BattleResult [target=%s, damage=%s, died=%s]", target, damage, dead);
	}

}