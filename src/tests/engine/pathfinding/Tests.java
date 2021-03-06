package tests.engine.pathfinding;

import static org.junit.Test.*;
import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import config.Config;



/**
 * Loads the loging properties
 * @author Bilal Hussain
 */
public class Tests {

	@BeforeClass
	public static void loadLogging(){
		Config.loadLoggingProperties("log4j-test.properties");
	}
	
}
