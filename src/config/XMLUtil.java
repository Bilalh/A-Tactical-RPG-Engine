package config;


import java.io.InputStream;
import java.io.StringWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import config.xml.SavedMap;
import config.xml.SavedTile;
import config.xml.TileImageData;
import config.xml.TileMapping;

/**
 *  Xml utilities  to create and parse xml 
 * @author Bilal Hussain
 */
public abstract class XMLUtil {


	private static XStream xs;
	
	static{
		
		xs = new XStream(new DomDriver());
		xs.processAnnotations(getClassesForAnnotations());
//		xs.addDefaultImplementation(Card.class,ICard.class); 
		xs.setMode(XStream.NO_REFERENCES);
	}
	
	/**
	 * Makes a xml string from a IPreference
	 * @param message - an instance of message 
	 * @return  A string containing the xml
	 */
	public static String makeXml(IPreference message){
		
		StringWriter sw = new StringWriter();
		xs.marshal(message, new CompactWriter(sw));
		return sw.toString();
	}
	
	// makes Formatted Xml
	public static String makeFormattedXml(IPreference message){
		
		return xs.toXML(message);
	}
	
	/**
	 *  Converts the xml in the string to an  object
	 * @param <E> - The type
	 * @param is  - The InputStream
	 * @return A message object
	 */
	public static <E> E convertXml(InputStream is){
		@SuppressWarnings("unchecked")
		E fromXML = (E) xs.fromXML(is);
		return fromXML;
	}

	
	/**
	 *  Converts the xml in the string to an  object
	 * @param <E> - The type
	 * @param s  - The String
	 * @return A message object
	 */
	public static <E>  E convertXml(String s){
		@SuppressWarnings("unchecked")
		E fromXML = (E) xs.fromXML(s);
		return fromXML;
	}
	
	public static void addAnnotations(Class<?>[] classes){
		xs.processAnnotations(classes);
	}
	
	/**
	 *  Get all the classes the  have Xstream Annotations
	 * @return A array of classes 
	 */
	public  static Class<?>[] getClassesForAnnotations() {
		
		return new Class[]{
			SavedTile.class,
			SavedMap.class,
			TileMapping.class,
			TileImageData.class
		};
	}
	
}
