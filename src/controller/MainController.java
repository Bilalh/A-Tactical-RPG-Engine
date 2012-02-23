package controller;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import common.interfaces.INotification;

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
	
	/** @category Constructor */
	public MainController() {
		this.engine = new Engine();
	}
	
	public MapController startMap(String name){
		Map map = engine.startMap(name);
		MapController mapController = new MapController(map);
		mapController.addObserver(this);
		return mapController;
	}
	
	@Override
	public void update(Observable o, Object n) {
		log.info("updated called");
		if (n instanceof INotification){
			((INotification) n).process(this);
		}
	}

	/** @category Generated */
	public void setGui(Gui gui) {
		this.gui = gui;
	}

	private int DEFAULT_FPS = 30;
	long period = (long) 1000.0 / DEFAULT_FPS;
	public void mapFinished() {
		log.info("mapFinished");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MapController mapController = startMap("maps/nicer.xml");
				gui.setCurrentPanel(new MapPanel(mapController, period * 1000000L));
			}
		});
		
	}	
	
	
	
}
