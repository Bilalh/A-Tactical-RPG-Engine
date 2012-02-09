package engine;

import java.util.*;
import java.util.Map.Entry;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitImages")
public class UnitImages implements IPreference {

	private String spriteSheetLocation;
	
	private HashMap<String, UnitImageData> animations;

	public UnitImages(){
		animations = new HashMap<String, UnitImageData>();
	}
	
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
		assert animations != null;
		assert key        !=null;
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
		assert animations != null;
		assert key   !=null;
		assert value !=null;
		return animations.put(key, value);
	}

	/** @category Generated */
	public int size() {
		return animations.size();
	}

	/** @category Generated */
	public UnitImageData remove(Object key) {
		return animations.remove(key);
	}

	/** @category Generated */
	public Set<Entry<String, UnitImageData>> entrySet() {
		return animations.entrySet();
	}
}
