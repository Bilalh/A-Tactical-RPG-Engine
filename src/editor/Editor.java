package editor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import common.gui.ResourceManager;
import common.spritesheet.SpriteSheet;

import config.Config;

import editor.editors.*;
import editor.util.Prefs;
import editor.util.Resources;
import engine.assets.*;
import engine.unit.UnitImages;

/**
 * Editor for the engine
 * @author Bilal Hussain
 */
public class Editor {
	private static final Logger log = Logger.getLogger(Editor.class);
	
	JFrame       frame;
	JTabbedPane  tabs;
	WeaponsPanel weaponsPanel;
	SkillsPanel  skillsPanel;
	UnitsPanel   unitPanel;
	
	UnitsImagesPanel unitImagesPanel;
	MapsPanel       mapsPanel;
	
	
	String projectPath = "projects/Test";
	String projectName = "Test";

	public Editor() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new JFrame("Tacical Engine Editor");
		frame.setContentPane((tabs = createTabs()));
		frame.setJMenuBar(createMenubar());
		
		Preferences pref = Prefs.getNode("main/panels/main");
		int width  = pref.getInt("width", 930);
		int height = pref.getInt("height", 800);
		
		frame.setMinimumSize(new Dimension(850, 700));
		
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

	
	public Weapons getWeapons(){
		return weaponsPanel.getWeapons();
	}

	public Skills getSkills(){
		return skillsPanel.getSkills();
	}
	
	java.util.Map<UUID, SpriteSheet> spriteSheets = Collections.synchronizedMap(new HashMap<UUID, SpriteSheet>());
	
	public UnitsImages getUnitImages(){
		UnitsImages images = new UnitsImages();
		images.put(Config.<UnitImages>loadPreference("images/characters/Boy-animations.xml"));
		images.put(Config.<UnitImages>loadPreference("images/characters/Elena-animations.xml"));
		images.put(Config.<UnitImages>loadPreference("images/characters/princess-animations.xml"));
		spriteSheets.clear();
		for (UnitImages ui : images.values()) {
			spriteSheets.put(ui.getUuid(), Config.loadSpriteSheet(ui.getSpriteSheetLocation()));
		}
		return images;
	}
	
	public java.util.Map<UUID, SpriteSheet> getSpriteSheets(){
		return Collections.unmodifiableMap(spriteSheets);
	}
	
	private JTabbedPane createTabs() {
		//TODO change
		File f = new File(projectPath);
		File mainXml  = new File(f, "tactical-project.xml");
		File resources = new File(f,"Resources");
		Config.setResourceDirectory(resources.getAbsolutePath() + "/");
		
		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");		

		JTabbedPane tabs  = new JTabbedPane();
		tabs.addTab("Weapons",      (weaponsPanel    = new WeaponsPanel()));
		tabs.addTab("Skills",       (skillsPanel     = new SkillsPanel()));
		tabs.addTab("Units",        (unitPanel       = new UnitsPanel(spriteSheets)));
		tabs.addTab("Unit Images",  (unitImagesPanel = new UnitsImagesPanel()));
		tabs.addTab("Maps",         (mapsPanel       = new MapsPanel()));
//		tabs.addTab("Story",        new JPanel());
//		tabs.addTab("Spritesheets", new JPanel());
//		tabs.addTab("Project",      new JPanel());
		
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
		          JTabbedPane tabs   = (JTabbedPane) e.getSource();
		          IRefreshable panel = (IRefreshable) tabs.getComponentAt(tabs.getSelectedIndex());
		          panel.panelSelected(Editor.this);
			}
		});
		
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

		JMenuItem open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,mask));
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		file.add(open);
		
		bar.add(file);
		return bar;
	}

	// Save the project
	void save(){
		
		File f = new File(projectPath);
		f.mkdir();
		
		File resources = new File(f,"Resources");
		resources.mkdir();
		Config.setResourceDirectory(resources.getAbsolutePath() + "/");
		
		// Assets
		File assets = new File(resources, "assets");
		assets.mkdir();

		Weapons ws =  weaponsPanel.getWeapons();
		Config.savePreferencesToResources(ws, "assets/weapons.xml");
		log.info(ws);

		Skills ss = skillsPanel.getSkills();
		Config.savePreferencesToResources(ss, "assets/skills.xml");
		log.info(ss);
		
		Units us = unitPanel.getUnits();
		Config.savePreferencesToResources(us, "assets/units.xml");
		log.info(us);
		
		// Images
		File images = new File(resources, "images");
		images.mkdir();
		File items = new File(images, "items");
		items.mkdir();
		
		// Main project file
		File mainXml  = new File(f, "tactical-project.xml");
		try {
			FileWriter fw = new FileWriter(mainXml);
			fw.write("");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Load the project
	void load(){
		File f = new File(projectPath);
		File mainXml  = new File(f, "tactical-project.xml");
		File resources = new File(f,"Resources");
		File assets = new File(resources, "assets");
		
		Config.setResourceDirectory(resources.getAbsolutePath() + "/");
		
		//TODO paths
		Weapons ws = Config.loadPreference("assets/weapons.xml");
		AssertStore.instance().loadWeapons(ws);
		weaponsPanel.setWeapons(ws);
		log.debug(ws);
		
		Skills ss = Config.loadPreference("assets/skills.xml");
		AssertStore.instance().loadSkill(ss);
		skillsPanel.setSkills(ss);
		log.info(ss);
		
		Units uu = Config.loadPreference("assets/units.xml");
		unitPanel.setUnits(uu);
		log.info(uu);
		
		IRefreshable panel = (IRefreshable) tabs.getComponentAt(tabs.getSelectedIndex());
        panel.panelSelected(Editor.this);
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
