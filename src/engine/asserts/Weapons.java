package engine.asserts;

import java.util.*;
import java.util.Map.Entry;

import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.interfaces.IWeapon;
import config.Config;
import config.IPreference;
import config.XMLUtil;
import engine.items.RangedWeapon;
import engine.items.Spear;

/**
 * Weapon assets -- to make the xml easier to read
 * @author Bilal Hussain
 */
@XStreamAlias("weapons")
public class Weapons extends AbstractAssets<IWeapon>{
	public static void main(String[] args) {
		
		Weapons ss = new Weapons();
		ss.put(new RangedWeapon(6, 5,3));
		ss.put(new Spear(5,3));
		
		String s = XMLUtil.makeFormattedXml(ss);
		System.out.println(s);
		
	}
	
}
