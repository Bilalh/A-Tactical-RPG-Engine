package config;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * printf style logging for log4j
 * @author Bilal Hussain
 */
public class LogF {

    private static final String FQCN = LogF.class.getName();

    
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

	public static <E> String array2d(E[][] arr,int startX, int endX, int startY, int endY, boolean newline){
		StringBuffer b = new StringBuffer(100);
		if (newline) b.append("\n");
		for (int i = startX; i < endX; i++) {
			b.append("[");
			for (int j = startX; j <endY; j++) {
				b.append(arr[i][j]);
				if (j != endY-1) b.append(", ");
			}
			b.append("]");
			if (i != endX-1) b.append("\n");
		}
		return b.toString();
	}
	
}
