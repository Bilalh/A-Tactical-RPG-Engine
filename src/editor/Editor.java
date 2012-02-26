package editor;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import config.Config;

import editor.util.Prefs;

/**
 * @author Bilal Hussain
 */
public class Editor {
	private static final Logger log = Logger.getLogger(Editor.class);
	
	JFrame frame;
	JPanel weaponsPanel;
	
	public Editor() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new JFrame("Tacical Engine Editor");
		frame.setContentPane(createContentPane());
		
		Preferences pref = Prefs.getNode("main/panels/main");
		int width  = pref.getInt("width", 930);
		int height = pref.getInt("height", 680);
		
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				onQuit();
			}
		}));
		frame.setVisible(true);
	}

	private Container createContentPane() {
		JTabbedPane tabs  = new JTabbedPane();
		tabs.addTab("Weapons", (weaponsPanel=createWeaponsPanel()));
		return tabs;
	}
	
	private JPanel createWeaponsPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));
		
		// Jlist of weapons 
		// Side pane of infomation
		
		// Add Weapon
		// Edit Weapon
		// Copy Weapons 
		
		return p;
	} 

	// Save window size and panel size 
	protected void onQuit() {
		log.info("Quiting");
		final int extendedState = frame.getExtendedState();
		final Preferences pref = Prefs.getNode("main/panels/main");
		pref.putInt("state", extendedState);
		if (extendedState == Frame.NORMAL) {
			pref.putInt("width", frame.getWidth());
			pref.putInt("height", frame.getHeight());
		}

		try {
			Prefs.root().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		log.info("Saved prefs" + Prefs.root());
	}
	
	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new Editor();
	}
}
