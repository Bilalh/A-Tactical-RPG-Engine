package config.assets;

import java.util.UUID;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.Identifiable;
import config.Config;
import config.IPreference;
import config.XMLUtil;
import engine.items.MeleeWeapon;

/**
 * A deferred assert stores a location of assert. 
 * 
 * When this object is serialised only the location is saved. 
 * 
 * The asset is loaded when the  it is requested.
 * 
 * @author Bilal Hussain
 */
//The asset is loaded when the this object is deserialised.
@XStreamAlias("deferredAsset")
public class DeferredAsset<E extends Identifiable & IPreference> implements Identifiable {

	@XStreamAsAttribute
	protected UUID uuid;
	protected transient E asset;
	protected String resouceLocation;
	
	public DeferredAsset(E asset, String resouceLocation) {
		this.uuid  = UUID.randomUUID();
		this.asset = asset;
		this.resouceLocation = resouceLocation;
	}
	
	public void reloadAsset(){
		if (resouceLocation == null) return;
		asset = (E) Config.loadPreference(resouceLocation);
	}
	
	public void saveAsset(){
		Config.savePreferences(asset, resouceLocation);
	}
	
	public E getAsset() {
		if (asset == null) reloadAsset();
		return asset;
	}

	/** @category Generated */
	public String getResouceLocation() {
		return resouceLocation;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}
}
