/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import openal.Music;
import openal.SlickException;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;

import controller.MainController;
import controller.MapController;

import view.map.MapPanel;
import view.ui.Console;
import view.ui.IConsole;

import engine.Engine;

/**
 * @author bilalh
 */
public class Gui {
	private static final Logger log = Logger.getLogger(Gui.class);


	public static int WIDTH;
	public static int HEIGHT;

	private MainController mainController;

	private JFrame frame;
	private MapPanel current;

	private int DEFAULT_FPS = 30;
	private long period = (long) 1000.0 / DEFAULT_FPS;
	private static IConsole debugConsole = new Console();

	static MusicThread music = new MusicThread();
	
	public Gui(int width, int height, MainController mainController) {
		log.info("Gui Creating");
		WIDTH  = width;
		HEIGHT = height;

		this.mainController = mainController;
		initialize();
		
//		MapController mapController = mainController.startMap("maps/fft2.xml");
//		setCurrentPanel(new MapPanel(mapController, period * 1000000L));
		
		music.start();
		frame.setResizable(true);
		frame.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// FIXME componentShown method
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				try {
					music.setMusic( new Music("music/3-15 Faraway Heights.ogg", true));
				} catch (SlickException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				music.toggleMusic();
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// FIXME componentHidden method
				
			}
		});
	}

	public Gui(MainController mainController) {
		this(800, 500, mainController);
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setCurrentPanel(MapPanel p) {
		assert p != null : "panel null";
		if (current != null) {
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

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public static IConsole console() {
		return debugConsole;
	}

	private static boolean showDebugConsole = false;

	public static boolean showDebugConsole() {
		return showDebugConsole;
	}

	public static void toggleDebugConsole() {
		showDebugConsole = !showDebugConsole;
	}

}
