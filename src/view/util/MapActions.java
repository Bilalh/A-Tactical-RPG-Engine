package view.util;

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

	/** @category Generated */
	public boolean isMouseMoving() {
		return mouseMoving;
	}

}
