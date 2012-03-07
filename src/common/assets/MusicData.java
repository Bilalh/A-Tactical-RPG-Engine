package common.assets;

import java.util.UUID;

import org.jvnet.inflector.Noun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.interfaces.Identifiable;

/**
 * Stores the location of the music
 * @author Bilal Hussain
 */
@XStreamAlias("music")
public class MusicData implements Identifiable {

	@XStreamAsAttribute
	private UUID uuid;
	private String location;

	public MusicData(){
		uuid = UUID.randomUUID();
	}
	
	/** @category Generated */
	public MusicData(String location) {
		this();
		this.location = location;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

	/** @category Generated */
	public String getLocation() {
		return location;
	}

	/** @category Generated */
	public void setLocation(String location) {
		this.location = location;
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
		if (!(obj instanceof MusicData)) return false;
		MusicData other = (MusicData) obj;
		if (uuid == null) {
			if (other.uuid != null) return false;
		} else if (!uuid.equals(other.uuid)) return false;
		return true;
	}
}
