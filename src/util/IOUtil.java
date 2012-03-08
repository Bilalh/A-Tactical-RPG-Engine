package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.swing.filechooser.FileFilter;

/**
 * File handing methods
 * @author Bilal Hussain
 */
public class IOUtil {

	public static String removeExtension(String name) {
		final int index = name.lastIndexOf('.');
		if (index == -1) return name;
		return name.substring(0, index);
	}

	public static String replaceExtension(String name, String ext) {
		final int index = name.lastIndexOf('.');
		if (index == -1) return name + ext;
		return name.substring(0, index) + ext;
	}

	public static FileFilter makeFileFilter(final boolean allowDirectories, final String extension, final String description) {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return (allowDirectories && f.isDirectory()) || f.getName().endsWith(extension);
			}

			@Override
			public String getDescription() {
				return description;
			}

		};
	}

	public static FileFilter makeRegexFileFilter(final boolean allowDirectories, final String regex, final String description) {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return (allowDirectories && f.isDirectory()) || f.getName().matches(regex);
			}

			@Override
			public String getDescription() {
				return description;
			}

		};
	}

	// based off https://gist.github.com/889747
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileInputStream fIn = null;
		FileOutputStream fOut = null;
		FileChannel source = null;
		FileChannel destination = null;
		try {
			fIn = new FileInputStream(sourceFile);
			source = fIn.getChannel();
			fOut = new FileOutputStream(destFile);
			destination = fOut.getChannel();
			long transfered = 0;
			long bytes = source.size();
			while (transfered < bytes) {
				transfered += destination.transferFrom(source, 0, source.size());
				destination.position(transfered);
			}
		} finally {
			if (source != null) {
				source.close();
			} else if (fIn != null) {
				fIn.close();
			}
			if (destination != null) {
				destination.close();
			} else if (fOut != null) {
				fOut.close();
			}
		}
	}

}
