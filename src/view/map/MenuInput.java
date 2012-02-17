package view.map;

import java.awt.event.MouseEvent;

import util.Logf;
import view.util.MapActions;

/**
 * @author Bilal Hussain
 */
public class MenuInput extends MapActions {

	public MenuInput(GuiMap map) {
		super(map);
	}

    @Override
	public void mousePressed(MouseEvent e) {
    	System.out.println(map.menu.clicked(e.getPoint()));
    }

	
}
