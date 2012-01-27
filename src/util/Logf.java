package util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * printf style logging for log4j
 * @author Bilal Hussain
 */
public class Logf {

    private static final String FQCN = Logf.class.getName();

    
	public static void trace(Logger log, String fmt, Object... args) {
		if (log.isEnabledFor(Level.TRACE)) {
	        log.callAppenders(new LoggingEvent(FQCN, log, Level.TRACE, String.format(fmt, args), null));
		}
	}

	public static void debug(Logger log, String fmt, Object... args) {
		if (log.isEnabledFor(Level.DEBUG)) {
	        log.callAppenders(new LoggingEvent(FQCN, log, Level.DEBUG, String.format(fmt, args), null));
		}
	}

	public static void info(Logger log, String fmt, Object... args) {
		if (log.isEnabledFor(Level.INFO)) {
	        log.callAppenders(new LoggingEvent(FQCN, log, Level.INFO, String.format(fmt, args), null));
		}
	}

	public static void warn(Logger log, String fmt, Object... args) {
		if (log.isEnabledFor(Level.WARN)) {
	        log.callAppenders(new LoggingEvent(FQCN, log, Level.WARN, String.format(fmt, args), null));
		}
	}

	public static void error(Logger log, String fmt, Object... args) {
		if (log.isEnabledFor(Level.ERROR)) {
	        log.callAppenders(new LoggingEvent(FQCN, log, Level.ERROR, String.format(fmt, args), null));
		}
	}

	
}
