package config.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * Maps from the tile type to the images.
 * @author Bilal Hussain
 */
@XStreamAlias("tilemapping")
public class TileMapping implements IPreference {

	@XStreamImplicit
	final private HashMap<String, String> tilemapping;
	
	/** @category Generated Constructor */
	public TileMapping(HashMap<String, String> tilemapping) {
		this.tilemapping = tilemapping;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("TileMapping [tilemapping=%s]", tilemapping != null ? toString(tilemapping.entrySet(), maxLen) : null);
	}

	/** @category Generated */
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
