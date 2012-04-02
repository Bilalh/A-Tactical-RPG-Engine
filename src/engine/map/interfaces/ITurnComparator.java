package engine.map.interfaces;

import java.util.Comparator;


/**
 * @author Bilal Hussain
 */
public interface ITurnComparator extends Comparator<IMutableMapUnit> {

	@Override
	int compare(IMutableMapUnit o1, IMutableMapUnit o2);

}