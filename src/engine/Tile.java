
package engine;

import java.awt.Point;

import common.interfaces.IUnit;


/**
 * @author bilalh
 */
public class Tile {

	private IUnit current ;
	private int startHeight;
	private int endHeight;
	private int cost;
	
	/** @category Generated */
	public Tile(int startHeight, int endHeight) {
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.cost = (int) (Math.random() *3 + 1);
	}
	
	/** @category Generated */
	public IUnit getCurrentUnit() {
		return current;
	}


	/** @category Generated */
	public void setCurrentUnit(IUnit current) {
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

	@Override
	public String toString() {
		return String.format("Tile [current=%s, startHeight=%s, endHeight=%s, cost=%s]", current,
				startHeight, endHeight, cost);
	} 
	
	
}
