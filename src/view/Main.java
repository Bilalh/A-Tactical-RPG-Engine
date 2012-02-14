package view;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import config.Config;
import controller.MainController;
import engine.Engine;

/**
 * @author Bilal Hussain
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	
	private MainController mainController;
	Gui window;
	
	public Main(){
		Config.loadLoggingProperties();
		System.setProperty("sun.java2d.opengl","True");
		try {
			EventQueue.invokeAndWait(new Runnable() {
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
		} catch (InterruptedException e) {
			// FIXME catch block in Main
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// FIXME catch block in Main
			e.printStackTrace();
		}
		log.info("Main returned");
		
		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		new Main();
	}
	
}
