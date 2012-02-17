package view.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import util.Logf;
import view.map.GuiMap.ActionsEnum;
import view.ui.Menu;
import view.ui.MenuItem;
import view.util.MapActions;

/**
 * @author Bilal Hussain
 */
public class MenuInput extends MapActions {

	private Menu menu;

	public MenuInput(GuiMap map, Menu menu) {
		super(map);
		this.menu = menu;
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		menu.draw(g, width - 100, height - 200);
	}

	@Override
	public void keyComfirm() {
		map.menuItemChosen(menu.getSelected());
	}

	@Override
	public void keyCancel() {
		map.setActionHandler(ActionsEnum.MOVEMENT);
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
		MenuItem mi = menu.clicked(e.getPoint());
		if (mi != null) map.menuItemChosen(mi);
		else           keyCancel();
	}

	/** @category Generated */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

}
