
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
	private int cost;
	
	/** @category Generated */
	public Tile(int startHeight, int endHeight) {
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.cost = (int) (Math.random() *2 + 1);
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

	/** @category Generated Getter */
	public int getCost() {
		return cost;
	} 
	
	
}
