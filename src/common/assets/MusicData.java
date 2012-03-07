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
}
