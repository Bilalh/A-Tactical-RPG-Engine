/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.util.Observable;

import javax.swing.JFrame;

import controller.MainController;
import controller.MapController;

import view.ui.Console;
import view.ui.IConsole;

import engine.Engine;


/**
 * @author bilalh
 */
public class Gui extends Observable {

	private MainController mainController;
	
	private JFrame frame;
	private MapPanel mapPanel;
	
	private int DEFAULT_FPS = 30;
	
	private static IConsole debugConsole = new Console();
	

	public Gui(MainController mainController) {
		this.mainController = mainController;
		initialize();
	}

	
	
	// Initialize the contents of the frame.
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 675, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		long period = (long) 1000.0 / DEFAULT_FPS;
		// System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
        		
		MapController mapController =  mainController.startMap("test");
		mapPanel = new MapPanel(mapController, period * 1000000L);
		frame.add(mapPanel);
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
