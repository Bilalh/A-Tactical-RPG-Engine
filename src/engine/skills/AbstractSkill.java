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


	@Override
	public int getPower() {
		return power;
	}


	@Override
	public void setPower(int power) {
		this.power = power;
	}


	@Override
	public boolean isTargetOpposite() {
		return targetOpposite;
	}


	@Override
	public void setTargetOpposite(boolean targetOpposite) {
		this.targetOpposite = targetOpposite;
	}


	@Override
	public boolean isIncludeCaster() {
		return includeCaster;
	}


	@Override
	public void setIncludeCaster(boolean includeCaster) {
		this.includeCaster = includeCaster;
	}

}