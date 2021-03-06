package config;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import util.Logf;

import com.sun.tools.internal.ws.util.xml.XmlUtil;

import common.enums.ImageType;
import common.spritesheet.SpriteSheet;

import config.xml.ITileMapping;
import config.xml.TileImageData;
import config.xml.TileMapping;
import editor.map.MapEditor;

/**
 * Class for loading preferences and spritesheets.  It also store the defaults 
 * @author Bilal Hussain
 */
public class Config {

	// Loging
	private static Properties defaultLoggingPrefs = new Properties();
	static {
		defaultLoggingPrefs.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		defaultLoggingPrefs.setProperty("log4j.rootLogger", "info, A1");
		defaultLoggingPrefs.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		defaultLoggingPrefs.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-5p [%t] %c: %m%n");
	}
	private static final String LOG_PROPERTIES_FILE = "./log4j.properties";
	private static Logger log = Logger.getLogger(Config.class);
	
	// Directory where  Resources are stored
	private static String RESOURCE_DIRECTORY = "./Resources/";
	
	
	public static Properties defaultLoggingProperties() {
		return defaultLoggingPrefs;
	}

	public static void loadLoggingProperties() {
		loadLoggingProperties(LOG_PROPERTIES_FILE);
	}
	
	public static void loadLoggingProperties(String filename){
		Properties p = new Properties(Config.defaultLoggingProperties());
		String error = null;
		try {
			p.load(new FileInputStream(new File(filename)));
		} catch (IOException e) {
			error = e.getLocalizedMessage();
		}
		PropertyConfigurator.configure(p);
		if (error != null) {
			log.warn("Log4J using defaults " + error);
		}else{
			log.info("Log4J loaded using " + filename);
		}		
	}
	
	/**
	 * Loads the Preference from the resource directory 
	 */
	public static <E extends IPreference> E loadPreference(String filename){
		return loadPreference(new File(RESOURCE_DIRECTORY + filename ));
	}

	public static <E extends IPreference> E loadPreference(File f){
		
		FileInputStream fio = null;
		try {
			 fio = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		E pref =  XMLUtil.convertXml(fio);
		return pref;
	}
	
	/**
	 * Loads the Preference from the classpath
	 */
	public static <E extends IPreference> E loadPreferenceFromClassPath(String path){
		InputStream io =  Config.class.getResourceAsStream(path);
		assert io != null;
		E pref = XMLUtil.convertXml(io);
		return pref;
	}
	
	/**
	 * Convert the Preference  to xml then write it to the stream 
	 */
	public static void  savePreferencesToStream(IPreference p, OutputStreamWriter w) throws IOException{
		String s = XMLUtil.makeFormattedXml(p);
		w.write(s);
		w.flush();
	}

	/**
	 * Convert the Preference to xml then write it to the filepath 
	 */
	public static void savePreferences(IPreference p, String path){
		String s = XMLUtil.makeXml(p);
		try {
			File f  = new File(RESOURCE_DIRECTORY + path);
			FileWriter fw = new FileWriter(f);
			savePreferencesToStream(p, fw);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			assert false : "Should not happen";
		}
	}
	
	/**
	 * Get the file based on the resource directory
	 */
	public static File getResourceFile(String path){
		 return new File(RESOURCE_DIRECTORY + path); 
	}

	/**
	 * Loads a spriteSheet from a filepath based on the resource directory
	 */
	public static SpriteSheet loadSpriteSheet(String filepath){
		File f = new File(RESOURCE_DIRECTORY+filepath);
		assert f.exists() : filepath + " does not exists";
		return loadSpriteSheet(f);
	}

	/**
	 * Loads a spriteSheet from a file
	 */
	public static SpriteSheet loadSpriteSheet(File in){
		File xml = new File(in.getParentFile(), in.getName().replaceAll("\\.png", "\\.xml"));
		assert xml.exists() : "xml not found";
		Logf.debug(log, "Try to load '%s' and '%s", in.getAbsolutePath(), xml.getAbsolutePath());
		SpriteSheet ss = null;
		try {
			BufferedImage b = ImageIO.read(in);
			assert b != null : in.getAbsolutePath();
			ss = new SpriteSheet(b, new FileInputStream(xml));
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert ss != null;
		return ss;
	}
	
	/**
	 * Return the filepath based on the resource directory as a inputstream
	 */
	public static InputStream getResourceAsStream(String ref){
		InputStream s = null;
		try {
			s = new FileInputStream(RESOURCE_DIRECTORY +ref);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assert s != null : ref + " not found";
		return s;
	}
	
	public static void setResourceDirectory(String path) {
		RESOURCE_DIRECTORY = path;
	}

	public static String getResourceDirectory(){
		return RESOURCE_DIRECTORY;
	}
	
	
}

