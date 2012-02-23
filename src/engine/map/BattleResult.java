package engine.map;

import engine.map.interfaces.IMutableMapUnit;

public class BattleResult {

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
	public IMutableMapUnit getTarget() {
		return target;
	}

	/** @category Generated */
	public int getDamage() {
		return damage;
	}

	/** @category Generated */
	public boolean isTargetDead() {
		return dead;
	}

	@Override
	public String toString() {
		return String.format("BattleResult [target=%s, damage=%s, died=%s]", target, damage, dead);
	}

}