package engine.map;

import org.apache.log4j.Logger;

import util.Logf;

import common.IBattleInfo;
import common.interfaces.IMapUnit;

import engine.map.interfaces.IMutableMapUnit;

/**
 * 
 * @author Bilal Hussain
 */
public class Battle implements IBattleInfo {
	private static final Logger log = Logger.getLogger(Battle.class);

	protected final IMutableMapUnit attacker;
	protected final IMutableMapUnit target;

	protected int damage;
	protected boolean targetAlive;

	public Battle(IMutableMapUnit attacker, IMutableMapUnit target) {
		this.attacker = attacker;
		this.target = target;
		calcuateBattle();
	}

	protected void calcuateBattle() {
		damage = calcuateDamage();
		Logf.info(log, "%s taking %s of %s", attacker, damage, target);
	}

	protected int calcuateDamage() {
		int result = attacker.getAttack() - target.getDefence();
		if (result < 0) result = 0;
		return result;
	}

	public void performBattle() {
		targetAlive = target.removeHp(damage);
	}

	/** @category Generated */
	@Override
	public IMapUnit getAttacker() {
		return attacker;
	}

	/** @category Generated */
	@Override
	public IMapUnit getTarget() {
		return target;
	}

	/** @category Generated */
	@Override
	public int getDamage() {
		return damage;
	}

	/** @category Generated */
	@Override
	public boolean isTargetAlive() {
		return targetAlive;
	}

	@Override
	public String toString() {
		return String.format("Battle [attacker=%s, target=%s, damage=%s, targetAlive=%s]", attacker, target, damage, targetAlive);
	}

}