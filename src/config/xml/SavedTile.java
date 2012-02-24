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
	final String type;
	
	final int height; 
	int startingHeight; // for the gui
	
	@XStreamAsAttribute
	final int x;
	@XStreamAsAttribute
	final int y;

	Orientation orientation;

	// to give default values
	private Object readResolve() {
		if (orientation == null)  orientation = Orientation.UP_TO_EAST;
		if (startingHeight == 0 && height != 0) startingHeight = height;
		return this;
	}

	public SavedTile(String type, int height, int x, int y, Orientation orientation) {
		this(type, height, height, x, y, orientation);
	}

	public SavedTile(String type, int startingHeight, int height, int x, int y, Orientation orientation) {
		this.x = x;
		this.y = y;
		
		this.type = type;
		this.orientation  = orientation;
		
		this.height = height;
		this.startingHeight = height;
		
	}
	
	/** @category Generated */
	public String getType() {
		return type;
	}

	/** @category Generated */
	public int getHeight() {
		return height;
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

	@Override
	public String toString() {
		return String.format("SavedTile [type=%s, height=%s, x=%s, y=%s, orientation=%s]", type, height, x, y, orientation);
	}

	/** @category Generated */
	public int getStartingHeight() {
		return startingHeight;
	}

}
