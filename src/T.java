import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import util.Logf;

import config.Config;


public class T {
	private static final String LOG_PROPERTIES_FILE = "log4j.properties";

	private static final org.apache.log4j.Logger log = Logger.getLogger(T.class);
	
	public static void main(String[] args) {
//		
//		Config.loadLoggingProperties();
//		log.trace("Trace Message!");
//		Logf.trace(log, "%s", "F Trace");
//		LogMF.trace(log, "{0}", "MF Trace");
//		log.debug("Debug Message!");
//		log.info("Info Message!");
//		log.warn("Warn Message!");
//		log.error("Error Message!");
//		log.fatal("Fatal Message!");
		for (int i = 0; i < 10; i++) {
			System.out.println(UUID.randomUUID());
		}
	}
}
