package controller;

import java.awt.EventQueue;
import java.util.*;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import common.interfaces.INotification;
import config.Config;
import config.assets.DeferredMap;
import config.assets.MapOrdering;
import config.assets.OrderedMap;

import view.Gui;
import view.map.MapPanel;
import engine.Engine;
import engine.map.Map;

/**
 * @author Bilal Hussain
 */
public class MainController extends Controller implements Observer {
	private static final Logger log = Logger.getLogger(MainController.class);

	private Gui gui;
	private Engine engine;

	private ArrayDeque<OrderedMap> ordering;

	public MainController() {
		this.engine = new Engine();
		this.ordering = new ArrayDeque<OrderedMap>();

		MapOrdering mo = Config.loadPreference("assets/ordering.xml");
		ArrayList<OrderedMap> al = new ArrayList(mo.values());
		Collections.sort(al);
		for (OrderedMap m : al) {
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

	public void mapFinished() {
		log.info("mapFinished");
		
		final MapController next = nextMap();
		if (next == null){
			JOptionPane.showMessageDialog(gui.getFrame(),"Game Completed", "Tactical RPG Engine", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				gui.setCurrentPanel(new MapPanel(next, period * 1000000L));
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
	
}
