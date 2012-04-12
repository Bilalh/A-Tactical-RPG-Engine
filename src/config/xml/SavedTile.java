package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import common.enums.Orientation;

import config.IPreference;

/**
 * An Xml representation of an tile.
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("tile")
public class SavedTile {
	protected final String type;
	
	protected final int height; 
	protected int startingHeight; // for the gui
	
	@XStreamAsAttribute
	protected final int x;
	@XStreamAsAttribute
	protected final int y;

	protected Orientation orientation;

	protected String leftWall;
	protected String rightWall;
	
	protected boolean impassable;
	
	// to give default values
	private Object readResolve() {
		if (orientation == null)  orientation = Orientation.TO_EAST;
		if (startingHeight == 0 && height != 0) startingHeight = height;
		return this;
	}

	public SavedTile(String type, int startingHeight, int endHeight, int x, int y, Orientation orientation,
			String leftWallRef, String rightWallRef) {
		this(type, startingHeight, endHeight, x, y, orientation, leftWallRef, rightWallRef, false);
	}

	public SavedTile(String type, int startingHeight, int endHeight, int x, int y, Orientation orientation,
			String leftWallRef, String rightWallRef, boolean impassable) {
		this.x = x;
		this.y = y;
		
		this.type         = type;
		this.orientation  = orientation;
		
		this.height         = endHeight;
		this.startingHeight = startingHeight;
		
		this.leftWall  = leftWallRef;
		this.rightWall = rightWallRef;
	}
	

	public String getType() {
		return type;
	}


	public int getEndHeight() {
		return height;
	}


	public int getStartingHeight() {
		return startingHeight;
	}
	

	public int getX() {
		return x;
	}


	public int getY() {
		return y;
	}


	public Orientation getOrientation() {
		return orientation;
	}

	@Override

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SavedTile)) return false;
		SavedTile other = (SavedTile) obj;
		if (height != other.height) return false;
		if (type == null) {
			if (other.type != null) return false;
		} else if (!type.equals(other.type)) return false;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}



	public String getLeftWallName() {
		return leftWall;
	}


	public String getRightWallName() {
		return rightWall;
	}

	@Override
	public String toString() {
		return String.format("SavedTile [type=%s, height=%s, startingHeight=%s, x=%s, y=%s, orientation=%s, impassable=%s, leftWall=%s, rightWall=%s]",
				type, height, startingHeight, x, y, orientation, impassable, leftWall, rightWall);
	}

	public boolean isImpassable() {
		return impassable;
	}


}
