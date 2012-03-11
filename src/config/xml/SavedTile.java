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
	
	// to give default values
	private Object readResolve() {
		if (orientation == null)  orientation = Orientation.TO_EAST;
		if (startingHeight == 0 && height != 0) startingHeight = height;
		return this;
	}

	public SavedTile(String type, int startingHeight, int endHeight, int x, int y, Orientation orientation,
			String leftWallRef, String rightWallRef) {
		this.x = x;
		this.y = y;
		
		this.type         = type;
		this.orientation  = orientation;
		
		this.height         = endHeight;
		this.startingHeight = startingHeight;
		
		this.leftWall  = leftWallRef;
		this.rightWall = rightWallRef;
		
	}
	
	/** @category Generated */
	public String getType() {
		return type;
	}

	/** @category Generated */
	public int getEndHeight() {
		return height;
	}

	/** @category Generated */
	public int getStartingHeight() {
		return startingHeight;
	}
	
	/** @category Generated */
	public int getX() {
		return x;
	}

	/** @category Generated */
	public int getY() {
		return y;
	}

	/** @category Generated */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	/** @category Generated */
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
	/** @category Generated */
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


	/** @category Generated */
	public String getLeftWallName() {
		return leftWall;
	}

	/** @category Generated */
	public String getRightWallName() {
		return rightWall;
	}

	@Override
	public String toString() {
		return String.format("SavedTile [type=%s, height=%s, startingHeight=%s, x=%s, y=%s, orientation=%s, leftWall=%s, rightWall=%s]",
				type, height, startingHeight, x, y, orientation, leftWall, rightWall);
	}


}
