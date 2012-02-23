package engine.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import common.Location;

import engine.map.interfaces.IMutableMapUnit;
import engine.skills.Skill;
import engine.unit.Unit;

/**
 * @author Bilal Hussain
 */
public class SkillBattle extends Battle {

	protected Skill skill;
	
	public SkillBattle(Skill skill, IMutableMapUnit attacker, Location target, Map map) {
		super(attacker);
		this.skill = skill;
		results = calcuateBattles(getTargets(attacker, target, map));
		assert !results.isEmpty();
	}

	protected Collection<IMutableMapUnit> getTargets(IMutableMapUnit attacker, Location target, Map map) {
		Collection<Location> c           =  skill.getArea(target, map.getFieldWidth(), map.getFieldHeight());
		HashSet<IMutableMapUnit> targets = new HashSet<IMutableMapUnit>();
		boolean type = attacker.isAI();
		if (skill.isTargetOpposite()) type = !type;
		
		for (Location l : c) {
			IMutableMapUnit u = map.getTile(l).getCurrentUnit();
			if (u != null) targets.add(u);
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
	
	
	
}
