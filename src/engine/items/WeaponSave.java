package engine.items;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import config.Config;
import config.XMLUtil;
import engine.unit.IWeapon;


/**
 * @author Bilal Hussain
 */
public class WeaponSave {

	public static void main(String[] args) {
		config.Config.loadLoggingProperties();
		
		String  s = XMLUtil.makeFormattedXml(new custom.CustomSpear(7, 10));
		System.out.println(s);
		IWeapon w = XMLUtil.convertXml(s);
		
		
//		ClassLoader cl = Config.class.getClassLoader();
//		IWeapon w2 = null;
//		try {
//			Class c = cl.loadClass("custom.Spear");
//			Constructor constructor = c.getConstructor(int.class, int.class);
//			w2 = (IWeapon) constructor.newInstance(5,2);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		
//		assert w2 != null;
		
	}
	
}
