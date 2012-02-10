package controller;

import java.util.Observable;
import java.util.Observer;

import view.Gui;
import engine.Engine;
import engine.ai.Map;

/**
 * @author Bilal Hussain
 */
public class MainController extends Controller implements Observer {

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
	public void update(Observable o, Object arg) {
		
	}

	/** @category Generated */
	public void setGui(Gui gui) {
		this.gui = gui;
	}	
	
	
	
}
