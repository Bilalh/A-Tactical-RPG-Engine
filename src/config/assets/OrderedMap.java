package config.assets;

import config.IPreference;
import config.xml.SavedMap;

/**
 * @author Bilal Hussain
 */
public class OrderedMap extends DeferredAsset<SavedMap> implements IPreference, Comparable<OrderedMap> {

	protected int index;

	/** @category Generated */
	public OrderedMap(DeferredAsset<SavedMap> da, int index) {
		super(da);
		this.index = index;
	}

	/** @category Generated */
	public int getIndex() {
		return index;
	}

	/** @category Generated */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int compareTo(OrderedMap o) {
		return this.index - o.index;
	}
	
}
