package engine;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("unitImageData")
public class UnitImageData {

	final private String name;
	final private int numberOfFrames;
	
	/** @category Generated */
	public UnitImageData(String name, int numberOfFrames) {
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
	
}
