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
