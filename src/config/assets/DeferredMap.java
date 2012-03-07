package config.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;
import config.xml.SavedMap;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("deferredMap")
public class DeferredMap extends DeferredAsset<SavedMap> implements IPreference {

	public DeferredMap(SavedMap asset, String resouceLocation) {
		super(asset, resouceLocation);
	}
	
}
