
package engine;

import java.awt.Point;

/**
 * @author bilalh
 */
public class Tile {

	private Unit current = null;
	private int height;
    private Point fieldLocation;
	
	/** @category Generated Constructor */
	public Tile(int height) {
		this.height = height;
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
	public int getHeight() {
		return height;
	} 
	
	
}
