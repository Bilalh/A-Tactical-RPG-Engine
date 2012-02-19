package view.map;

import view.ui.MenuItem;

/**
 * @author Bilal Hussain
 */
public class MapMenuItem extends MenuItem {

	private UnitState state;

	public MapMenuItem(String name, UnitState state) {
		super(name);
		this.state = state;
	}

	/** @category Generated */
	public UnitState getState() {
		return state;
	}

}
