
package engine.map;

import common.Location;

import common.interfaces.IUnit;
import config.xml.TileImageData;


/**
 * @author bilalh
 */
public class Tile {

	private IUnit current ;
	private int startHeight;
	private int endHeight;
	private int cost;
	
	private String type;
	
	/** @category Generated */
	public Tile(int startHeight, int endHeight,String type) {
		this.startHeight = startHeight;
		this.endHeight   = endHeight;
		this.cost        = endHeight;
		this.type        = type;
	}
	
	// for testing
	Tile(int startHeight, int endHeight) {
		this(startHeight, endHeight, "grass");
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

	/** @category Generated Getter */
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("Tile [current=%s, startHeight=%s, endHeight=%s, cost=%s, type=%s]",
				current, startHeight, endHeight, cost,type);
	}
}
