package util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Bilal Hussain
 */
public class IOUtil {

	public static String removeExtension(String name){
		final int index  = name.lastIndexOf('.');
		if(index == -1 ) return name;
		return name.substring(0, index);
	}
	
	 
	
	public static FileFilter makeFileFilter(final boolean allowDirectories, final String extension, final String description){
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return  (allowDirectories && f.isDirectory()) || f.getName().endsWith(extension);
			}

			@Override
			public String getDescription() {
				return description;
			}

		};
	}

	public static FileFilter makeRegexFileFilter(final boolean allowDirectories, final String regex, final String description){
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return  (allowDirectories && f.isDirectory()) || f.getName().matches(regex);
			}

			@Override
			public String getDescription() {
				return description;
			}

		};
	}
	
}
