package config.xml;

import java.util.Arrays;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("map")
public class SavedMap implements IPreference {

	@XStreamAsAttribute
	final int width;
	@XStreamAsAttribute
	final int height;

	@XStreamImplicit
	final SavedTile[] tiles;

	@XStreamAlias("tilemapping")
	final String tileMapping;

	/** @category Generated Constructor */
	public SavedMap(int width, int height, SavedTile[] tiles, String tileMapping) {
		this.width = width;
		this.height = height;
		this.tiles = tiles;
		this.tileMapping = tileMapping;
	}

	/** @category Generated Getter */
	public int getWidth() {
		return width;
	}

	/** @category Generated Getter */
	public int getHeight() {
		return height;
	}

	/** @category Generated Getter */
	public SavedTile[] getTiles() {
		return tiles;
	}

	@Override
	public String toString() {
		return String.format("SavedMap [width=%s, height=%s, tiles=%s]", width, height, Arrays.deepToString(tiles));
	}

}
