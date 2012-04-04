package config.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("mapOrdering")
public class MapOrdering extends AbstractAssets<OrderedMap> {
	
	public List<OrderedMap> sortedValues() {
		List<OrderedMap> l = new ArrayList(values());
		Collections.sort(l);
		return l;
	}
	
}
