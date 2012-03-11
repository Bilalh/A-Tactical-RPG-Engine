package engine.unit;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import util.IOUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.interfaces.Identifiable;

import config.IPreference;

/**
 * Stores data about a spritesheet
 * @author Bilal Hussain
 */
@XStreamAlias("unitImages")
public class SpriteSheetData implements IPreference, Identifiable {

	private String spriteSheetLocation;
	private HashMap<String, UnitAnimation> animations;
	
	@XStreamAsAttribute
	private UUID uuid;

	public SpriteSheetData(){
		animations = new HashMap<String, UnitAnimation>();
		uuid = UUID.randomUUID();
	}
	
	public SpriteSheetData(String spriteSheetLocation) {
		this();
		assert spriteSheetLocation.endsWith(".png");
		this.spriteSheetLocation = spriteSheetLocation;
	}
	
	private Object readResolve() {
		if (uuid ==null){
			assert false : spriteSheetLocation + " has no UUID"; 
			uuid = UUID.randomUUID();
		}
		return this;
	}
	
	public String getAnimationPath(){
		String path = getSpriteSheetLocation();
		// remove the file extension
		path = path.substring(0, path.lastIndexOf('.'));
		// Add the unitImages path
		path += "-animations.xml";
		return path;
	}
	
	/** @category Generated */
	public String getSpriteSheetLocation() {
		assert spriteSheetLocation.endsWith(".png"): spriteSheetLocation;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SpriteSheetData)) return false;
		SpriteSheetData other = (SpriteSheetData) obj;
		if (uuid == null) {
			if (other.uuid != null) return false;
		} else if (!uuid.equals(other.uuid)) return false;
		return true;
	}

	@Override
	public String toString() {
		return IOUtil.removeExtension(new File(getSpriteSheetLocation()).getName());
	}
	
	
}
