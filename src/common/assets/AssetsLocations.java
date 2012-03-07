package common.assets;

/**
 * Stores the location of all the assets.
 * 
 * @author Bilal Hussain
 */
public class AssetsLocations {

	public final String weaponsPath;
	public final String skillsPath;
	public final String musicLocation;
	public final String soundsLocation;
	
	public AssetsLocations(String weaponsPath, String skillsPath, String musicLocation, String soundsLocation) {
		this.weaponsPath = weaponsPath;
		this.skillsPath = skillsPath;
		this.musicLocation = musicLocation;
		this.soundsLocation = soundsLocation;
	}

}