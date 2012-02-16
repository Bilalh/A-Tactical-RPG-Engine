package view;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import config.Config;
import controller.MainController;
import engine.Engine;

/**
 * Runs the game
 * @author Bilal Hussain
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	
	private MainController mainController;
	private Gui window;
	
	public Main(){
		Config.loadLoggingProperties();
		System.setProperty("sun.java2d.opengl","True");
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						mainController = new MainController();
						window = new Gui(mainController);
						mainController.setGui(window);
						window.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		new Main();
	}
	
}
