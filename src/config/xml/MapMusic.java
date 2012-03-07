package config.xml;

import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.Config;
import config.IPreference;
import config.assets.MusicData;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("mapMusic")
public class MapMusic implements IPreference {
	private UUID backgroundId;

	/** @category Generated */
	public MapMusic(UUID backgroundId) {
		this.backgroundId = backgroundId;
	}

	/** @category Generated */
	public UUID getBackgroundId() {
		return backgroundId;
	}

	/** @category Generated */
	public void setBackgroundId(UUID backgroundId) {
		this.backgroundId = backgroundId;
	}

	public static void main(String[] args) {
		Config.savePreferences(new MapMusic(UUID.fromString("af6f534a-ba3f-4d4a-b809-0882f8f95176")), "default-music.xml");
	}
}
