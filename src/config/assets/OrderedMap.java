package config.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;
import config.xml.SavedMap;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("orderedMap")
public class OrderedMap extends DeferredAsset<SavedMap> implements IPreference, Comparable<OrderedMap> {

	protected int index;


	public OrderedMap(DeferredAsset<SavedMap> da, int index) {
		super(da);
		this.index = index;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int compareTo(OrderedMap o) {
		return this.index - o.index;
	}
	
}
