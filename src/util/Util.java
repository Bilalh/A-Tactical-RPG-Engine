package util;

import config.Config;

/**
 * @author Bilal Hussain
 */
public class Util {

	public static <E> E getClassInstancebyName(String name){
		ClassLoader cl = Config.class.getClassLoader();
		Class c = null;
		try {
			c = cl.loadClass("custom.Spear");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		assert c != null;
		E e = null;
		
		try {
			e = (E) c.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		assert e != null;
		return e;
	}
	
}
