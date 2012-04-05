package controller;

import java.awt.EventQueue;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import common.interfaces.INotification;
import config.Config;
import config.assets.DeferredMap;
import config.assets.MapOrdering;
import config.assets.OrderedMap;
import config.assets.Units;

import view.Gui;
import view.map.MapPanel;
import engine.Engine;
import engine.map.Map;
import engine.unit.IMutableUnit;

/**
 * Handles the overall game flow
 * @author Bilal Hussain
 */
public class MainController extends Controller implements Observer {
	private static final Logger log = Logger.getLogger(MainController.class);

	private Gui gui;
	private Engine engine;

	private ArrayDeque<OrderedMap> ordering;

	MapOrdering allMaps;
	
	public MainController() {
		this.engine = new Engine();
		this.ordering = new ArrayDeque<OrderedMap>();

		allMaps = Config.loadPreference("assets/ordering.xml");
		for (OrderedMap m : allMaps.sortedValues()) {
			ordering.addLast(m);
		}

	}

	public MapController nextMap() {
		OrderedMap m = ordering.pollFirst();
		if (m == null) {
			System.err.println("Game finished");
			return null;
		}
		return startMap(m.getResouceLocation());
	}

	public MapController startMap(String name) {
		Map map = engine.startMap(name);
		MapController mapController = new MapController(map);
		mapController.addObserver(this);
		return mapController;
	}

	private int DEFAULT_FPS = 30;
	private long period = (long) 1000.0 / DEFAULT_FPS;

	public void mapFinished(boolean won) {
		log.info("mapFinished");
		
		if (won == false){
//			JOptionPane.showMessageDialog(gui.getFrame(),"Game Over", "Tactical RPG Engine", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
		
		final MapController next = nextMap();
		if (next == null){
			JOptionPane.showMessageDialog(gui.getFrame(),"Game Completed", "Tactical RPG Engine", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setCurrentPanel(new MapPanel(next, period * 1000000L,gui.getMapWidth(), gui.getMapHeight()));
			}
		});

	}

	@Override
	public void update(Observable o, Object n) {
		log.info("updated called");
		if (n instanceof INotification) {
			((INotification) n).process(this);
		}
	}

	public void setGui(Gui gui) {
		this.gui = gui;
	}

	public void save() {
		int index = ordering.peek().getIndex() - 1;
		MapOrdering newOrdering = new MapOrdering();
		log.debug("Current index:" + index);
		for (OrderedMap m : allMaps) {
			if (m.getIndex() >= index)  newOrdering.put(m);
		}
		
		log.debug("newOrdering:" + newOrdering);

		try {
			File saveDir = new File(System.getProperty("user.home"), ".tactical");
			saveDir.mkdir();
			File fOrdering = new File(saveDir, "progress.xml");
			File fUnits    = new File(saveDir, "units.xml");
			Config.savePreferencesToStream(newOrdering, new FileWriter(fOrdering));
			
			Units uu =  new Units();
			for (IMutableUnit u : engine.getPlayer().getUnits()) {
				uu.put(u);
			}
			
			Config.savePreferencesToStream(uu, new FileWriter(fUnits));
		} catch (IOException e) {
			log.info("Saved failed");
			e.printStackTrace();
		}
		
	}

	public void load() {
		File saveDir = new File(System.getProperty("user.home"), ".tactical");
		if (!saveDir.exists()) return;
		
		File fOrdering = new File(saveDir, "progress.xml");
		File fUnits    = new File(saveDir, "units.xml");
		if (!fOrdering.exists() || !fUnits.exists()) return;
		
		allMaps = Config.loadPreference(fOrdering);
		Units uu = Config.loadPreference(fUnits);
		ordering.clear();
		for (OrderedMap m : allMaps.sortedValues()) {
			ordering.addLast(m);
		}
		mapFinished(true);
	}
	
}
