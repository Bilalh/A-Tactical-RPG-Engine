package engine.map.ai;

import engine.map.AIPlayer;
import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class LowestStrength extends AbstractTargetOrdering {

	public LowestStrength(){
		super();
	}

	@Override
	public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
		int r = o1.getStrength() - o2.getStrength();
		if (r == 0) r = o1.getCurrentHp() - o2.getCurrentHp();
		return r;
	}

	@Override
	public String toString() {
		return "Target the unit with the lowest Strength";
	}
	
}
