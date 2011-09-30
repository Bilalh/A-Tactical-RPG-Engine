
package engine;

import java.awt.Point;

/**
 * @author bilalh
 */
public class Tile {

	private Unit current = null;
	private int startHeight;
	private int endHeight;

	/** @category Generated Constructor */
	public Tile(int startHeight, int endHeight) {
		this.startHeight = startHeight;
		this.endHeight = endHeight;
	}
	
	/** @category Generated Getter */
	public Unit getCurrent() {
		return current;
	}


	/** @category Generated Setter */
	public void setCurrent(Unit current) {
		this.current = current;
	}
	/** @category Generated Getter */
	public int getStartHeight() {
		return startHeight;
	}

	/** @category Generated Getter */
	public int getEndHeight() {
		return endHeight;
	} 
	
	
}
