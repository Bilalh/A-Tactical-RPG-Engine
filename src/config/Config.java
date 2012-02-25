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
import editor.MapEditor;

/**
 * Class for loading preferences and spritesheets.  It also store the defaults 
 * @author Bilal Hussain
 */
public class Config {

	private static Properties defaultLoggingPrefs = new Properties();
	static {
		defaultLoggingPrefs.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		defaultLoggingPrefs.setProperty("log4j.rootLogger", "debug, A1");
		defaultLoggingPrefs.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		defaultLoggingPrefs.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-5p [%t] %c: %m%n");
	}

	private static final String LOG_PROPERTIES_FILE = "./log4j.properties";
	private static String RESOURCE_DIRECTORY = "./Resources/";
	
	
	private static Logger log = Logger.getLogger(Config.class);
	
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
	
	public static <E extends IPreference> E loadPreference(String filename){
		
		FileInputStream fio = null;
		try {
			 fio = new FileInputStream(new File(RESOURCE_DIRECTORY + filename ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		E pref =  XMLUtil.convertXml(fio);
		return pref;
	}

	public static <E extends IPreference> E loadPreferenceFromClassPath(String path){
		InputStream io =  Config.class.getResourceAsStream(path);
		assert io != null;
		E pref = XMLUtil.convertXml(io);
		return pref;
	}
	
	public static void  savePreferences(IPreference p, OutputStreamWriter w) throws IOException{
		String s = XMLUtil.makeFormattedXml(p);
		w.write(s);
	}

	public static void savePreferencesToResources(IPreference p, String path){
		String s = XMLUtil.makeXml(p);
		try {
			FileWriter fw = new FileWriter(new File(RESOURCE_DIRECTORY + path));
			savePreferences(p, fw);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			assert false : "Should not happen";
		}
	}
	
	
	public static SpriteSheet loadSpriteSheet(String filepath){
		return loadSpriteSheet(new File(RESOURCE_DIRECTORY+filepath));
	}

	public static SpriteSheet loadSpriteSheet(File in){
		File xml = new File(in.getParentFile(), in.getName().replaceAll("\\.png", "\\.xml"));
		Logf.debug(log, "Try to load '%s' and '%s", in.getAbsolutePath(), xml.getAbsolutePath());
		SpriteSheet ss = null;
		try {
			BufferedImage b = ImageIO.read(in);
			ss = new SpriteSheet(b, new FileInputStream(xml));
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert ss != null;
		return ss;
	}
	
	public static void setResourceDirectory(String path) {
		RESOURCE_DIRECTORY = path;
	}
	
	
	private static final ITileMapping defaultMapping;
	static{
		HashMap<String, TileImageData> m = new HashMap<String, TileImageData>();
		TileImageData d= new TileImageData("images/tiles/brown.png", ImageType.NON_TEXTURED);
		m.put("*", d );
		m.put("grass", d);
		defaultMapping = new TileMapping("",m);
	}
	
	public static ITileMapping defaultMapping(){
		return defaultMapping;
	}

	
}

