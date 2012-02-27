package editor;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
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
	WeaponsPanel weaponsPanel;
	
	public Editor() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new JFrame("Tacical Engine Editor");
		frame.setContentPane(createContentPane());
		frame.setJMenuBar(createMenubar());
		
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
		tabs.addTab("Weapons", (weaponsPanel = new WeaponsPanel()));
		return tabs;
	}

	private JMenuBar createMenubar() {
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,mask));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		file.add(save);
		bar.add(file);
		return bar;
	}

	void save(){
		
	}
	
	// Save window size and panel size 
	void onQuit() {
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
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new Editor();
	}
}
