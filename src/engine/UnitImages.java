package engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitImages")
public class UnitImages implements IPreference {

	private String spriteSheetLocation;
	
	@XStreamImplicit
	private HashMap<String, UnitImageData> animations = new HashMap<String, UnitImageData>();


	/** @category Generated */
	public String getSpriteSheetLocation() {
		return spriteSheetLocation;
	}

	/** @category Generated */
	public void setSpriteSheetLocation(String spriteSheetLocation) {
		this.spriteSheetLocation = spriteSheetLocation;
	}
	
	/** @category Generated */
	public boolean containsKey(Object key) {
		return animations.containsKey(key);
	}

	/** @category Generated */
	public UnitImageData get(Object key) {
		return animations.get(key);
	}

	/** @category Generated */
	public boolean isEmpty() {
		return animations.isEmpty();
	}

	/** @category Generated */
	public UnitImageData put(String key, UnitImageData value) {
		return animations.put(key, value);
	}

	/** @category Generated */
	public int size() {
		return animations.size();
	}
}
