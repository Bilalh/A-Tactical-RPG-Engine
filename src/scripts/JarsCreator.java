package scripts;

import org.ninjacave.jarsplice.core.Splicer;

/**
 * Creates the required jars for exporting to (windows/linux)
 * @author Bilal Hussain
 */
public class JarsCreator {

	private static final String[] natives = new String[] {
			"libs/OpenAL32.dll",
			"libs/jinput-dx8.dll",
			"libs/jinput-raw.dll",
			"libs/libjinput-linux.so",
			"libs/libjinput-linux64.so",
			"libs/libjinput-osx.jnilib",
			"libs/liblwjgl.jnilib",
			"libs/liblwjgl.so",
			"libs/liblwjgl64.so",
			"libs/libopenal.so",
			"libs/lwjgl.dll",
			"libs/openal.dylib",
	};
	
	private static final String[] engineJars = new String[] {
			"engine.jar",
			"libs/jl1.0.1.jar",
			"libs/jogg-0.0.7.jar",
			"libs/jorbis-0.0.15.jar",
			"libs/log4j-1.2.16.jar",
			"libs/lwjgl.jar",
			"libs/miglayout-4.0-swing.jar",
			"libs/xpp3_min-1.1.4c.jar",
			"libs/xstream-1.4.2.jar",
	};
	
	private static final String[] editorJars = new String[] {
			"editor.jar",
			"libs/Inflector.jar",
			"libs/commons-io-2.1.jar",
			"libs/inflector-0.7.0-javadoc.jar",
			"libs/jaudiotagger-2.0.4-20111207.115108-15.jar",
			"libs/javarichclient_icon_tango_action_1.0.jar",
			"libs/jl1.0.1.jar",
			"libs/jogg-0.0.7.jar",
			"libs/jorbis-0.0.15.jar",
			"libs/junit-4.10.jar",
			"libs/log4j-1.2.16.jar",
			"libs/lwjgl.jar",
			"libs/miglayout-4.0-swing.jar",
			"libs/xpp3_min-1.1.4c.jar",
			"libs/xstream-1.4.2.jar",
	};
	
	Splicer splicer = new Splicer();
	
    public void createJars(){
    	// Create the jars (the method of the library is badly named)
    	splicer.createMacApp(engineJars, natives, "bundle/Tactical.jar",        "view.Main", "");
        splicer.createMacApp(editorJars, natives, "Binaries/jar/Tactical Engine.jar", "editor.ProjectChooser", "");
    }
   
    public static void main(String[] args) {
		new JarsCreator().createJars();
	}
    
}
