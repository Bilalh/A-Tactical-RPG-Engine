package engine.asserts;

import java.util.*;
import java.util.Map.Entry;

import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import common.interfaces.IWeapon;
import config.Config;
import config.IPreference;
import config.XMLUtil;
import engine.items.RangedWeapon;
import engine.items.Spear;

/**
 * Weapon assets 
 * @author Bilal Hussain
 */
@XStreamAlias("weapons")
public class Weapons implements IPreference, IAssets<IWeapon> {
	@XStreamImplicit
	private Map<UUID, IWeapon> weapons = new HashMap<UUID, IWeapon>();

	public static void main(String[] args) {
		
		Weapons ss = new Weapons();
		ss.put(new RangedWeapon(6, 5,3));
		ss.put(new Spear(5,3));
		
		String s = XMLUtil.makeFormattedXml(ss);
		System.out.println(s);
//		Config.savePreferencesToResources(ss, "assets/weapons.xml");
		
	}

	@Override
	public IWeapon put(IWeapon w) {
		return weapons.put(w.getUuid(), w);
	}
	
	/** @category Generated */
	@Override
	public void putAll(Map<? extends UUID, ? extends IWeapon> arg0) {
		weapons.putAll(arg0);
	}

	/** @category Generated */
	@Override
	public IWeapon remove(UUID id) {
		return weapons.remove(id);
	}

	/** @category Generated */
	@Override
	public void clear() {
		weapons.clear();
	}
	
	/** @category Generated */
	@Override
	public boolean containsKey(UUID id) {
		return weapons.containsKey(id);
	}

	/** @category Generated */
	@Override
	public Collection<IWeapon> values() {
		return weapons.values();
	}

	/** @category Generated */
	@Override
	public Map<UUID, IWeapon> getMap() {
		return weapons;
	}

	/** @category Generated */
	@Override
	public int size() {
		return weapons.size();
	}

	/** @category Generated */
	@Override
	public boolean isEmpty() {
		return weapons.isEmpty();
	}
	
}
