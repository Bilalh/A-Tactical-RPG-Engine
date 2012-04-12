package common.enums;

/**
 * Stores the values for each Direction and their inverses
 * @author Bilal Hussain
 */
public enum Direction {
	SOUTH (0, -1, "S",1),
	NORTH (0,  1, "N", 0),
	
	WEST  (-1, 0, "W",3),
	EAST  (1,  0, "E",2),
	
	STILL (0,  0, "-",4);

	/** The displacement in x for the direction*/
	public final int x;
	/** The displacement in y for the direction*/
	public final int y;
	/** The name to use for debug */
	public final String shortName;
	/** The inverse direction*/
	private final int inverse;
	
	private Direction(int x, int y, String shortName, int inverse) {
		this.x = x;
		this.y = y;
		this.shortName = shortName;
		this.inverse   = inverse;
	}

	/**
	 * Returns the inverse of this direction
	 */
	public Direction inverse(){
		return values()[inverse];
	}
	
	/**
	 * Returns a reference string, this string will never be changed so it can be used for image filenames for example.
	 */
	public String reference() {
		return this.name().toLowerCase();
	}
	
	@Override
	public String toString() {
		return shortName;
	}
	
}