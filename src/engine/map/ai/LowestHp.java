package engine.map.ai;

import java.util.ArrayList;

import engine.map.AIPlayer;
import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class LowestHp extends AbstractAIBehaviour {

	public LowestHp(){
		super();
	}
	
	public LowestHp(Map map, AIPlayer ai, MapPlayer player) {
		super(map, ai, player);
	}

	@Override
	public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
		int r = o1.getCurrentHp() - o2.getCurrentHp();
		if (r == 0) r = o2.getMaxHp() - o1.getMaxHp();
		if (negated) r = -r;
		return r;
	}

	@Override
	public String toString() {
		return String.format("Target the unit with the %s HP", negated ? "highest" : "lowest" );
	}

}
