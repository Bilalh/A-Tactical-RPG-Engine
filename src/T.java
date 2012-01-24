import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import config.Config;


public class T {
	private static final String LOG_PROPERTIES_FILE = "log4j.properties";

	private static final org.apache.log4j.Logger log = Logger.getLogger(T.class);
	
	public static void main(String[] args) {
		
		Config.loadLoggingProperties();
		log.trace("Trace Message!");
		log.debug("Debug Message!");
		log.info("Info Message!");
		log.warn("Warn Message!");
		log.error("Error Message!");
		log.fatal("Fatal Message!");
	}
}
