package engine.map;

import common.interfaces.IBattleResult;
import common.interfaces.IMapUnit;

import engine.map.interfaces.IMutableMapUnit;

/**
 * Holds the result from a battle.
 * 
 * @author Bilal Hussain
 */
public class BattleResult implements IBattleResult {

	private IMutableMapUnit target;
	private final int damage;
	private final boolean dead;

	public BattleResult(IMutableMapUnit target, int damage, boolean died) {
		this.target = target;
		this.damage = damage;
		this.dead = died;
	}

	@Override
	public IMapUnit getTarget() {
		return target;
	}

	IMutableMapUnit getMutableTarget() {
		return target;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public boolean isTargetDead() {
		return dead;
	}

	@Override
	public String toString() {
		return String.format("BattleResult [target=%s, damage=%s, died=%s]", target, damage, dead);
	}

}