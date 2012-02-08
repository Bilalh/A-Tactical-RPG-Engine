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

	public final int x;
	public final int y;
	public final String shortName;
	private final int inverse;
	
	private Direction(int x, int y, String shortName, int inverse) {
		this.x = x;
		this.y = y;
		this.shortName = shortName;
		this.inverse   = inverse;
	}

	public Direction inverse(){
		return values()[inverse];
	}
	
	public String reference() {
		return this.name().toLowerCase();
	}
	
	@Override
	public String toString() {
		return shortName;
	}
	
}