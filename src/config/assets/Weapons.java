package config.assets;

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
import engine.skills.ISkill;
import engine.skills.RangedSkill;

/**
 * A subclass is used since the generic type is lost due to type erasure. 
 * @author Bilal Hussain
 */
@XStreamAlias("weapons")
public class Weapons extends AbstractAssets<IWeapon>{
	public static void main(String[] args) {
		
//		Weapons ss = new Weapons();
//		ss.put(new RangedWeapon(6, 5,3));
//		ss.put(new Spear(5,3));
		
		Skills ss = new Skills();
		ss.put(new RangedSkill("Air Blade",     10, 2,  0, true,false));
		ss.put(new RangedSkill("Thunder Flare", 15, 3,  1, true,false));
		ss.put(new RangedSkill("Thunderbird",   40, 4,  2, true,false));
		
		String s = XMLUtil.makeFormattedXml(ss);
		System.out.println(s);
	
		Config.savePreferences(ss, "assets/skills.xml");
		
	}
	
}
