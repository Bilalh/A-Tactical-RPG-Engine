
package engine;

import java.awt.Point;

import common.interfaces.IUnit;


/**
 * @author bilalh
 */
public class Tile {

	private IUnit current = null;
	private int startHeight;
	private int endHeight;

	/** @category Generated */
	public Tile(int startHeight, int endHeight) {
		this.startHeight = startHeight;
		this.endHeight = endHeight;
	}
	
	/** @category Generated */
	public IUnit getCurrent() {
		return current;
	}


	/** @category Generated */
	public void setCurrent(IUnit current) {
		this.current = current;
	}
	/** @category Generated */
	public int getStartHeight() {
		return startHeight;
	}

	/** @category Generated */
	public int getEndHeight() {
		return endHeight;
	} 
	
	
}
