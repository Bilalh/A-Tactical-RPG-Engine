package engine.map;

import java.util.Comparator;

import common.interfaces.IMapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.map.interfaces.ITurnComparator;

/**
 * Class that defines how the units turn
 * @author Bilal Hussain
 */
public class DefaultTurnComparator implements ITurnComparator {

	@Override
	public int compare(IMutableMapUnit o1, IMutableMapUnit o2) {
		 int r = o2.getReadiness() - o1.getReadiness();
		 if (r == 0) r = o2.getSpeed() - o1.getSpeed();
		 return r;
	}

}
