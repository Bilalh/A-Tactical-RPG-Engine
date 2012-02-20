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

	// Get the callers of the method that called this method (for debuging)
	public static String getCallers(int number){
		assert number >0;
        final StackTraceElement[] ste =Thread.currentThread().getStackTrace();
        StringBuffer b = new StringBuffer(50);
        for (int i = 2, j = Math.min(ste.length, 2+number); i <j; i++) {
//			b.append(ste[i].getMethodName()).append(":").append(ste[i].getLineNumber());
//			b.append(ste[i].getMethodName()).append("(").append(ste[i].getFileName()).append(":").append(ste[i].getLineNumber()).append(")");
        	b.append(ste[i].getClassName()).append(".").append(ste[i].getMethodName()).append("(").append(ste[i].getFileName()).append(":").append(ste[i].getLineNumber()).append(")");
			if (i != j -1) b.append(", ");
		}
        return b.toString();
	}
}
