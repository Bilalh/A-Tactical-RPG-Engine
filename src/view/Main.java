package view;

import java.awt.EventQueue;

import config.Config;
import controller.MainController;
import engine.Engine;

/**
 * @author Bilal Hussain
 */
public class Main {

	private MainController mainController;
	
	public Main(){
		Config.loadLoggingProperties();
		System.setProperty("sun.java2d.opengl","True");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					mainController = new MainController();
					Gui window = new Gui(mainController);
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
