package engine;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Store details on the animation
 * @author Bilal Hussain
 */
@XStreamAlias("unitAnimation")
public class UnitAnimation {

	final private String name;
	final private int numberOfFrames;
	
	/** @category Generated */
	public UnitAnimation(String name, int numberOfFrames) {
		this.name = name;
		this.numberOfFrames = numberOfFrames;
	}

	/** @category Generated */
	public String getName() {
		return name;
	}

	/** @category Generated */
	public int getNumberOfFrames() {
		return numberOfFrames;
	}

	@Override
	public String toString() {
		return String.format("%s - %s", name, numberOfFrames);
	}
	
}
