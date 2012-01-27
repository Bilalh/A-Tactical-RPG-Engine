package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("tile")
public class SavedTile{
	final String type;
	final int height;

	@XStreamAsAttribute
	final int x;
	@XStreamAsAttribute
	final int y;

	/** @category Generated Constructor */
	public SavedTile(String type, int height, int x, int y) {
		this.type = type;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	/** @category Generated Getter */
	public String getType() {
		return type;
	}

	/** @category Generated Getter */
	public int getHeight() {
		return height;
	}

	/** @category Generated Getter */
	public int getX() {
		return x;
	}

	/** @category Generated Getter */
	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format("SavedTile [type=%s, height=%s, x=%s, y=%s]", type, height, x, y);
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
	
}
