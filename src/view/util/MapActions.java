package view.util;

import java.awt.Graphics;
import java.awt.Graphics2D;

import view.map.GuiMap;

/**
 * @author Bilal Hussain
 */
public class MapActions extends ActionsAdapter {

	protected GuiMap map;
	protected boolean mouseMoving;

	public MapActions(GuiMap map) {
		this.map = map;
	}


	public boolean isMouseMoving() {
		return mouseMoving;
	}

	public void draw(Graphics2D g, int width, int height) {}
}
