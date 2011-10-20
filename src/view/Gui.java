/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

import view.ui.Console;
import view.ui.IConsole;

import engine.Engine;


/**
 * @author bilalh
 */
public class Gui {

	private JFrame frame;
	private MapPanel mapPanel;
	private GuiMap map;
	private Engine engine;
	
	private static IConsole debugConsole = new Console();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	private int DEFAULT_FPS = 50;
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 675, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		long period = (long) 1000.0 / DEFAULT_FPS;
		// System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
        
		engine = new Engine();
		engine.Map map =  engine.startMap("test");
		
		mapPanel = new MapPanel(map, period * 1000000L);
        
		
		frame.add(mapPanel);
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
