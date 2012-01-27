package config;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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

	private static final String LOG_PROPERTIES_FILE = "log4j.properties";
	private static Logger log = Logger.getLogger(Config.class);
	
	public static Properties defaultLoggingProperties() {
		return defaultLoggingPrefs;
	}

	public static void loadLoggingProperties() {
		loadLoggingloadLoggingProperties(LOG_PROPERTIES_FILE);
	}
	
	public static void loadLoggingloadLoggingProperties(String filename){
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
	
}
