package engine.skills;

import java.util.UUID;


/**
 * Provide reasonable defaults for a skill
 * @author Bilal Hussain
 */
public abstract class AbstractSkill extends AbstractUnitObject implements ISkill {

	protected int power;
	protected boolean targetOpposite;
	protected boolean includeCaster;
	
	public AbstractSkill() {
		power = 1;
		targetOpposite = true;
		includeCaster  = false;
	}

	/** @category Generated */
	@Override
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public int getPower() {
		return power;
	}

	/** @category Generated */
	public void setPower(int power) {
		this.power = power;
	}

	/** @category Generated */
	@Override
	public boolean isTargetOpposite() {
		return targetOpposite;
	}

	/** @category Generated */
	public void setTargetOpposite(boolean targetOpposite) {
		this.targetOpposite = targetOpposite;
	}

	/** @category Generated */
	@Override
	public boolean isIncludeCaster() {
		return includeCaster;
	}

	/** @category Generated */
	public void setIncludeCaster(boolean includeCaster) {
		this.includeCaster = includeCaster;
	}

}