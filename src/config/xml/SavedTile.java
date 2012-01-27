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

}
