package config;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.tools.internal.ws.util.xml.XmlUtil;

import common.enums.ImageType;

import config.xml.TileImageData;
import config.xml.TileMapping;

/**
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
	private static final String RESOURCE_DIRECTORY = "./Resources/";
	
	
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

	public static <E extends IPreference> E loadMap(String filename){
		return loadPreference("maps" + File.separator + filename);
	}

	private static final TileMapping defaultMapping;
	
	static{
		HashMap<String, TileImageData> m = new HashMap<String, TileImageData>();
		TileImageData d= new TileImageData("images/tiles/brown.png", ImageType.NON_TEXTURED);
		m.put("*", d );
		m.put("grass", d);
		defaultMapping = new TileMapping(m);
	}
	
	public static TileMapping defaultMapping(){
		return defaultMapping;
	}
	
}

