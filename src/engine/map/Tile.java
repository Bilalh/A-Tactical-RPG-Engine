package engine.map;

import common.Location;

import common.enums.Orientation;
import common.interfaces.IUnit;
import config.xml.TileImageData;

/**
 * @author bilalh
 */
public class Tile {

	protected IMutableMapUnit current;
	protected int startHeight;
	protected int endHeight;
	protected int cost;

	protected String type;
	protected Orientation orientation;

	public Tile(int startHeight, int endHeight, String type, Orientation orientation) {
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.type = type;
		this.orientation = orientation;
		this.cost = endHeight;
	}

	/** @category Generated */
	public IMutableMapUnit getCurrentUnit() {
		return current;
	}

	/** @category Generated */
	public void setCurrentUnit(IMutableMapUnit current) {
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

	/** @category Generated */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return String.format("Tile [current=%s, startHeight=%s, endHeight=%s, cost=%s, type=%s]",
				current, startHeight, endHeight, cost, type);
	}

}
