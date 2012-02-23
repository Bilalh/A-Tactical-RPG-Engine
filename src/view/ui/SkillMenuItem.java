package view.ui;

import engine.unit.Skill;

/**
 * @author Bilal Hussain
 */
public class SkillMenuItem extends MenuItem {

	protected Skill skill;

	public SkillMenuItem(Skill skill) {
		super(skill.getName());
		this.skill = skill;
	}

	/** @category Generated */
	public Skill getSkill() {
		return skill;
	}

}
