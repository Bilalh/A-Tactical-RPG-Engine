/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import controller.MainController;
import controller.MapController;

import view.map.MapPanel;
import view.ui.Console;
import view.ui.IConsole;

import engine.Engine;


/**
 * @author bilalh
 */
public class Gui extends Observable {
	private static final Logger log = Logger.getLogger(Gui.class);
	private MainController mainController;
	
	public static int WIDTH = 675;
	public static int HEIGHT = 450;
	
	private JFrame frame;
	private MapPanel current;

	
	private int DEFAULT_FPS = 30;
	long period = (long) 1000.0 / DEFAULT_FPS;
	private static IConsole debugConsole = new Console();
		

	public Gui(MainController mainController) {
		log.info("Gui Creating");
		this.mainController = mainController;
		initialize();
	}

	
	public void setCurrentPanel(MapPanel p) {
		assert p != null : "panel null";
		if (current != null) {
			log.info("Current Finished");
			 current.finished();
			 frame.remove(current);
		}
		log.info("Setting main panel to " + p);
		current = p;
		current.setBounds(0, 0, WIDTH, HEIGHT);
		
		frame.add(current);
		frame.invalidate();
		frame.repaint();
	}
	
	// Initialize the contents of the frame.
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		// System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
		
		MapController mapController =  mainController.startMap("maps/fft2.xml");
		setCurrentPanel(new MapPanel(mapController, period * 1000000L));
	}
	
	public void setVisible(boolean b){
		frame.setVisible(b);
	}
	
	public static IConsole console() {
		return debugConsole;
	}

	private static boolean showDebugConsole = false;
	public static boolean showDebugConsole() {
		return showDebugConsole;
	}    
	
	public static void toggleDebugConsole(){
		showDebugConsole = !showDebugConsole;
	}
	
}
