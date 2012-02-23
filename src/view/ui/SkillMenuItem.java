package view.ui;

import engine.skills.ISkill;

/**
 * @author Bilal Hussain
 */
public class SkillMenuItem extends MenuItem {

	protected ISkill skill;

	public SkillMenuItem(ISkill skill) {
		super(skill.getName());
		this.skill = skill;
	}

	/** @category Generated */
	public ISkill getSkill() {
		return skill;
	}

}
