package view.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.GuiMap.ActionsEnum;
import view.ui.Menu;
import view.ui.interfaces.IMenu;
import view.ui.interfaces.IMenuItem;
import view.util.MapActions;

/**
 * Handles input for the menus as well as drawing the menu 
 * @author Bilal Hussain
 */
public class MenuInput extends MapActions {
	private static final Logger log = Logger.getLogger(MenuInput.class);
	
	private IMenu menu;

	public MenuInput(GuiMap map, IMenu menu) {
		super(map);
		this.menu = menu;
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		menu.draw(g, width - menu.getWidth()-5, height - 200);
	}

	@Override
	public void keyComfirm() {
		Logf.info(log, "exec: %s",map.getState());
		map.changeState(map.getState().exec());
	}

	@Override
	public void keyCancel() {
		Logf.info(log, "cancel: %s",map.getState());
		map.changeState(map.getState().cancel());
	}

	@Override
	public void keyUp() {
		menu.scrollToPrevious();
	}

	@Override
	public void keyDown() {
		menu.scrollToNext();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		IMenuItem mi = menu.getClickedItem(e.getPoint());
		
		if (mi != null){
			Logf.info(log, "exec: %s",map.getState());
			map.changeState(map.getState().exec());
		}else{
			keyCancel();
		}
	}

	/** @category Generated */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

}
