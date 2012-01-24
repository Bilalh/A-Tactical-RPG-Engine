package config;

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 * This software is published under the terms of the Apache Software */
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * DateFormatFileAppender is a log4j Appender and extends {@link FileAppender} so each log is named
 * based on a date format defined in the File property.
 * 
 * Sample File: 'logs/'yyyy/MM-MMM/dd-EEE/HH-mm-ss-S'.log' Makes a file like:
 * logs/2004/04-Apr/13-Tue/09-45-15-937.log
 * 
 * @author James Stauffer
 * 
 * @author Bilal Hussain 
 *  Added specify logging directory 
 * 
 */
public class DateFormatFileAppender extends FileAppender {

	/**
	 * The default constructor does nothing.
	 */
	public DateFormatFileAppender() {
	}

	/**
	 * Instantiate a <code>DailyRollingFileAppender</code> and open the file designated by
	 * <code>filename</code>. The opened filename will become the ouput destination for this
	 * appender.
	 */
	public DateFormatFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename, true);
	}

	private String fileBackup;// Saves the file pattern
	private boolean separate = false;

	@Override
	public void setFile(String file) {
		super.setFile(file);
		this.fileBackup = getFile();
	}

	/**
	 * If true each LoggingEvent causes that file to close and open. This is useful when the file is
	 * a pattern that would often produce a different filename.
	 */
	public void setSeparate(boolean separate) {
		this.separate = separate;
	}

	@Override
	protected void subAppend(LoggingEvent event) {
		if (separate) {
			try {// First reset the file so each new log gets a new file.
				setFile(getFile(), getAppend(), getBufferedIO(), getBufferSize());
			} catch (IOException e) {
				LogLog.error("Unable to reset fileName.");
			}
		}
		super.subAppend(event);
	}

	@Override
	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO,int bufferSize) throws IOException {
		String[] arr= fileBackup.split("%");
		SimpleDateFormat sdf = new SimpleDateFormat(arr[0]);
		String prefix = arr.length >=2 ? arr[1] : "";
		String suffix = arr.length >=3 ? arr[2] : "";
		String actualFileName = prefix+ sdf.format(new Date()) + suffix;
		makeDirs(actualFileName);
		super.setFile(actualFileName, append, bufferedIO, bufferSize);
	}

	/**
	 * Ensures that all of the directories for the given path exist. Anything after the last / or \
	 * is assumed to be a filename.
	 */
	private void makeDirs(String path) {
		int indexSlash = path.lastIndexOf("/");
		int indexBackSlash = path.lastIndexOf("\\");
		int index = Math.max(indexSlash, indexBackSlash);
		if (index > 0) {
			String dirs = path.substring(0, index);
			// LogLog.debug("Making " + dirs);
			File dir = new File(dirs);
			if (!dir.exists()) {
				boolean success = dir.mkdirs();
				if (!success) {
					LogLog.error("Unable to create directories for " + dirs);
				}
			}
		}
	}

}
