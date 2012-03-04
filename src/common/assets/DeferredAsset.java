package common.assets;

import java.util.UUID;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.Identifiable;
import config.Config;
import config.XMLUtil;
import engine.items.MeleeWeapon;

/**
 * A deferred assert stores a location of assert. 
 * 
 * When this object is serialised only the location is saved. 
 * The asset is loaded when the this object is deserialised.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("deferredAsset")
public class DeferredAsset<E extends Identifiable> implements Identifiable {

	@XStreamAsAttribute
	protected UUID uuid;
	protected transient E asset;
	protected String resouceLocation;
	
	public DeferredAsset(E asset, String resouceLocation) {
		uuid = UUID.randomUUID();
		this.asset = asset;
		this.resouceLocation = resouceLocation;
	}

	// to load resource
	private Object readResolve() {
		asset = (E) Config.loadPreference(resouceLocation);
		return this;
	}
	
	public void reloadAsset(){
		asset = (E) Config.loadPreference(resouceLocation);
	}
	
	/** @category Generated */
	public E getAsset() {
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
