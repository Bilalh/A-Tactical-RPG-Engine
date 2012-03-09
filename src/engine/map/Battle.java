package engine.map;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import util.Logf;

import common.interfaces.IBattleInfo;
import common.interfaces.IMapUnit;

import engine.map.interfaces.IMutableMapUnit;

/**
 * Combat system for attacks
 * @author Bilal Hussain
 */
public class Battle implements IBattleInfo {
	private static final Logger log = Logger.getLogger(Battle.class);

	protected final IMutableMapUnit attacker;
	protected Collection<BattleResult> results;
	protected Map map;
	
	/**
	 * Must initialise results if using this
	 */
	protected Battle(IMutableMapUnit attacker){
		assert attacker != null;
		this.attacker = attacker;
	}
	
	public Battle(IMutableMapUnit attacker, IMutableMapUnit target, Map map) {
		this(attacker);

		assert target   != null;
		assert map      != null;
		this.map = map;
		this.results  = calcuateBattles(getTargets(attacker, target, map));
	}

	protected Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, IMutableMapUnit target, Map map){
		return attacker.getWeapon().getTargets(attacker, target, map);
	}
	
	protected Collection<BattleResult> calcuateBattles(Collection<IMutableMapUnit> targets) {
		Collection<BattleResult> results = new ArrayList<BattleResult>();
		for (IMutableMapUnit target : targets) {
			int damage = calcuateDamage(target);
			results.add(new BattleResult(target, damage, target.willDie(damage)));
			Logf.info(log, "%s taking %s of %s", attacker, damage, target);
		}
		return results;
	}

	protected int calcuateDamage(IMutableMapUnit target) {
		double modifer = getAttackModifer(target); 
		int result = (int) ((attacker.getAttack() * modifer) - target.getDefence());
		if (result < 0) result = 0;
		return result;
	}
	
	// Get any bonus/penalty for the attacker
	private double getAttackModifer(IMutableMapUnit target) {
		int aHeight =  map.getTile(attacker.getLocation()).getEndHeight();
		int tHeight =  map.getTile(target.getLocation()).getEndHeight();
		return heightModifier(target, aHeight, tHeight);
	}

	protected double heightModifier(IMutableMapUnit target, int attackerHeight, int targetHeight){
		double modifer = 1+0.1*(attackerHeight-targetHeight);
		return modifer;
	}
	
	public void performBattle() {
		for (BattleResult battle : results) {
			battle.getMutableTarget().removeHp(battle.getDamage());
		}
	}

	@Override
	public IMapUnit getAttacker() {
		return attacker;
	}

	@Override
	public Collection<BattleResult> getResults() {
		return results;
	}
	
	@Override
	public String toString() {
		return String.format("Battle [attacker=%s, results=%s]", attacker, results);
	}

}