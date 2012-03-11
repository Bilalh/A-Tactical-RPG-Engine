package engine.map;

import common.Location;

import common.enums.Orientation;
import common.interfaces.IUnit;
import config.xml.SavedTile;
import config.xml.TileImageData;
import engine.map.interfaces.IMutableMapUnit;

/**
 * Stores infomation about a tile
 * @author Bilal Hussain
 */
public class Tile {

	protected IMutableMapUnit current;
	protected int startHeight;
	protected int endHeight;
	protected int cost;

	protected String type;
	protected Orientation orientation;
	
	protected String leftWall;
	protected String rightWall;
	
	private Tile(int startHeight, int endHeight, String type, Orientation orientation, 
			String leftWall, String rightWall) {
		this.orientation = orientation;
		this.startHeight = startHeight;
		this.endHeight   = endHeight;
		this.rightWall   = rightWall;
		this.leftWall    = leftWall;
		this.type        = type;
		this.cost        = endHeight;
	}


	public Tile(SavedTile t) {
		this(t.getStartingHeight(), t.getEndHeight(), t.getType(), t.getOrientation(), 
				t.getLeftWallName(), t.getRightWallName());
	}
	
	public Tile(Tile t){
		this(t.getStartingHeight(), t.getEndHeight(), t.getType(), t.getOrientation(), 
				t.getLeftWallName(), t.getRightWallName());
	}
	
	public Tile(int startHeight, int endHeight, String type, Orientation orientation) {
		this(startHeight, endHeight, type, orientation, null, null);
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
	public int getStartingHeight() {
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

	/** @category Generated */
	public String getLeftWallName() {
		return leftWall;
	}

	/** @category Generated */
	public void setLeftWall(String leftWall) {
		this.leftWall = leftWall;
	}

	/** @category Generated */
	public String getRightWallName() {
		return rightWall;
	}

	/** @category Generated */
	public void setRightWall(String rightWall) {
		this.rightWall = rightWall;
	}

}
