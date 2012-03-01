package engine.unit;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.interfaces.Identifiable;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitImages")
public class UnitImages implements IPreference, Identifiable {

	private String spriteSheetLocation;
	private HashMap<String, UnitAnimation> animations;
	
	@XStreamAsAttribute
	private UUID uuid;

	public UnitImages(){
		animations = new HashMap<String, UnitAnimation>();
		uuid = UUID.randomUUID();
	}
	
	public UnitImages(String spriteSheetLocation) {
		this();
		this.spriteSheetLocation = spriteSheetLocation;
	}
	
	private Object readResolve() {
		if (uuid ==null) uuid = UUID.randomUUID();
		return this;
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
	public UnitAnimation get(Object key) {
		return animations.get(key);
	}

	/** @category Generated */
	public boolean isEmpty() {
		return animations.isEmpty();
	}

	/** @category Generated */
	public UnitAnimation put(String key, UnitAnimation value) {
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
	public UnitAnimation remove(Object key) {
		return animations.remove(key);
	}

	/** @category Generated */
	public Set<Entry<String, UnitAnimation>> entrySet() {
		return animations.entrySet();
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}
	
}
