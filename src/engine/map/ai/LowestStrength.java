package engine.map.ai;

import engine.map.AIPlayer;
import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class LowestStrength extends AbstractAIBehaviour {

	public LowestStrength(){
		super();
	}

	@Override
	public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
		int r = o1.getStrength() - o2.getStrength();
		if (r == 0) r = o1.getCurrentHp() - o2.getCurrentHp();
		if (negated) r = -1;
		return r;
	}

	@Override
	public String toString() {
		return String.format("Target the unit with the %s Strength", negated ? "highest" : "lowest" );
	}
	
}
