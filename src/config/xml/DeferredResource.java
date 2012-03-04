package config.xml;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.Identifiable;
import config.Config;
import config.XMLUtil;
import engine.assets.AssertStore;
import engine.items.MeleeWeapon;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("deferredResouce")
public class DeferredResource<E extends Identifiable> {

	private transient E resource;
	private String resouceLocation;

	/** @category Generated */
	private DeferredResource(E resource, String resouceLocation) {
		this.resource = resource;
		this.resouceLocation = resouceLocation;
	}

	// to load resource
	private Object readResolve() {
		
		resource = (E) Config.loadPreference(resouceLocation);
		return this;
	}
	
	/** @category Generated */
	public E getResource() {
		return resource;
	}

	/** @category Generated */
	public String getResouceLocation() {
		return resouceLocation;
	}
}
