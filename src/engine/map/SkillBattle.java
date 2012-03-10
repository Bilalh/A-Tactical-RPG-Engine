package engine.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import util.Logf;

import common.Location;

import engine.map.interfaces.IMutableMapUnit;
import engine.skills.ISkill;
import engine.unit.Unit;

/**
 * Combat system for Skills
 * @author Bilal Hussain
 */
public class SkillBattle extends Battle {
	private static final Logger log = Logger.getLogger(SkillBattle.class);
	
	protected ISkill skill;
	
	public SkillBattle(ISkill skill, IMutableMapUnit attacker, Location target, Map map) {
		super(attacker);
		this.skill = skill;
		results = calcuateBattles(getTargets(attacker, target, map));
		assert !results.isEmpty();
	}

	protected Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, Location target, Map map) {
		Collection<Location> c =  skill.getArea(target, map.getFieldWidth(), map.getFieldHeight());
		assert !c.isEmpty();
		Logf.debug(log, "Skill Area %s", c);
		HashSet<IMutableMapUnit> targets = new HashSet<IMutableMapUnit>();
		boolean type = attacker.isAI();
		if (skill.isTargetOpposite()) type = !type;
		
		for (Location l : c) {
			IMutableMapUnit u = map.getTile(l).getCurrentUnit();
			if (u != null){
				targets.add(u);
			}
		}

		targets = removeFromTargets(targets);
		assert !targets.isEmpty();
		return targets;
	}

	
	
	/**
	 * Remove targets who should not be effected e.g the attacker
	 */
	protected HashSet<IMutableMapUnit> removeFromTargets( HashSet<IMutableMapUnit> targets){
		targets.remove(attacker);
		return targets;
	}
	
	@Override
	protected int calcuateDamage(IMutableMapUnit target) {
		int result = skill.getPower() - target.getDefence();
		if (result < 0) result = 0;
		return result;
	}

	// height does not affect skills
	@Override
	protected double heightModifier(IMutableMapUnit target, int attackerHeight, int targetHeight) {
		return 1;
	}
	
}
