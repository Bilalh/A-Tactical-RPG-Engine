package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JPanel;


import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;

import controller.MainController;
import controller.MapController;

import util.openal.Music;
import util.openal.SlickException;
import view.map.MapPanel;
import view.ui.Console;
import view.ui.interfaces.IConsole;

import engine.Engine;

/**
 * The Gui
 * @author Bilal Hussain
 */
public class Gui {
	private static final Logger log = Logger.getLogger(Gui.class);

	private MainController mainController;

	private JFrame frame;
	private MapPanel currentPanel;

	private int DEFAULT_FPS = 30;
	private long period = (long) 1000.0 / DEFAULT_FPS;
	
	private static IConsole console = new Console();
	private static boolean showConsole = false;

	private static KeysPanel keysPanel;
	
	private static MusicThread musicThread = new MusicThread();
	
	private int mapWidth;
	private int mapHeight;
	
	
	public Gui(int width, int height, MainController mainController) {
		log.info("Gui Creating");
		this.setMapWidth(width);
		this.setMapHeight(height);
		this.mainController = mainController;
		
		initialize();
		musicThread.start();
		
		MapController mapController = mainController.nextMap();
		setCurrentPanel(new MapPanel(mapController, period * 1000000L, width, height));

		keysPanel = new KeysPanel();
		keysPanel.setVisible(true);
		keysPanel.setLocation(frame.getX()+frame.getWidth(), frame.getY());
	}

	public Gui(MainController mainController) {
		this(800, 500, mainController);
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setBounds(100, 100, getMapWidth(), getMapHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setCurrentPanel(MapPanel p) {
		assert p != null : "panel null";
		if (currentPanel != null) {
			currentPanel.finished();
			frame.remove(currentPanel);
		}
		log.info("Setting main panel to " + p);
		currentPanel = p;
		
		// FIXME mac only?
		currentPanel.setBounds(0, -22, getMapWidth(), getMapHeight());
		Rectangle b = currentPanel.getBounds();
		
				
		frame.add(currentPanel);
		frame.invalidate();
		frame.repaint();
		p.setFocusable(true);
		p.requestFocus();
	}

	public static void showKeyMapping(){
		if (!keysPanel.isVisible()){
			keysPanel.setVisible(true);
		}
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public static IConsole console() {
		return console;
	}

	public static boolean showConsole() {
		return showConsole;
	}

	public static void toggleConsole() {
		showConsole = !showConsole;
	}

	public static MusicThread getMusicThread() {
		return musicThread;
	}

	public Component getFrame() {
		return frame;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
}
