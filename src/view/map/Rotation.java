package view.map;

/**
 * Postion of 0,0 on the map 
 * @author Bilal Hussain
 */
public enum Rotation{
	WEST,
	NORTH,
	EAST,
	SOUTH;
	
	/**
	 * Returns the next rotation
	 */
	public Rotation next(){
		return values()[(ordinal() +1) % values().length];
	}
	
}