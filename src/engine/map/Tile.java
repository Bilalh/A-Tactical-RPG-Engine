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
	
	protected boolean impassable;
	
	private Tile(int startHeight, int endHeight, String type, Orientation orientation, 
			String leftWall, String rightWall, boolean impassable) {
		this.orientation = orientation;
		this.startHeight = startHeight;
		this.endHeight   = endHeight;
		this.rightWall   = rightWall;
		this.leftWall    = leftWall;
		this.type        = type;
		this.cost        = endHeight;
		this.impassable  = impassable;
	}

	public Tile(SavedTile t) {
		this(t.getStartingHeight(), t.getEndHeight(), t.getType(), t.getOrientation(), 
				t.getLeftWallName(), t.getRightWallName(), t.isImpassable());
	}
	
	public Tile(Tile t){
		this(t.getStartingHeight(), t.getEndHeight(), t.getType(), t.getOrientation(), 
				t.getLeftWallName(), t.getRightWallName(), t.isImpassable());
	}
	
	public Tile(int startHeight, int endHeight, String type, Orientation orientation) {
		this(startHeight, endHeight, type, orientation, null, null, false);
	}


	public IMutableMapUnit getCurrentUnit() {
		return current;
	}


	public void setCurrentUnit(IMutableMapUnit current) {
		this.current = current;
	}


	public int getStartingHeight() {
		return startHeight;
	}


	public int getEndHeight() {
		return endHeight;
	}


	public int getCost() {
		return cost;
	}


	public String getType() {
		return type;
	}


	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return String.format("Tile [current=%s, startHeight=%s, endHeight=%s, cost=%s, type=%s, orientation=%s, impassable=%s]", current,
				startHeight, endHeight, cost, type, orientation, impassable);
	}


	public String getLeftWallName() {
		return leftWall;
	}


	public void setLeftWall(String leftWall) {
		this.leftWall = leftWall;
	}


	public String getRightWallName() {
		return rightWall;
	}


	public void setRightWall(String rightWall) {
		this.rightWall = rightWall;
	}


	public boolean isImpassable() {
		return impassable;
	}

}
