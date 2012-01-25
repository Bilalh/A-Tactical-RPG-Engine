package common;

public enum Direction {
	NORTH (0,  1, "N"),
	SOUTH (0, -1, "S"),
	WEST  (-1, 0, "W"),
	EAST  (1,  0, "E"), 
	STILL (0,  0, "-");

	public final int x;
	public final int y;
	public final String shortName;
	
	private Direction(int x, int y, String shortName) {
		this.x = x;
		this.y = y;
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return shortName;
	}
	
	
}