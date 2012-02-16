package view.map;

import common.enums.Direction;

/**
 * The Rotation specifes the location of 0,0 on the map.
 * This enum also handles converting the direction based on the rotation.
 * @author Bilal Hussain
 */
public enum Rotation{
	WEST {
		@Override
		Direction transtateDirection(Direction current) {
			return current;
		}
	},
	NORTH {
		@Override
		Direction transtateDirection(Direction current) {
			switch(current){
				case NORTH: return Direction.SOUTH;
				case SOUTH: return Direction.NORTH;
				case EAST:  return Direction.EAST;
				case WEST:  return Direction.WEST;
			}
			return current;
		}
	},
	EAST {
		@Override
		Direction transtateDirection(Direction current) {
			switch(current){
				case NORTH: return Direction.SOUTH;
				case SOUTH: return Direction.NORTH;
				case EAST:  return Direction.WEST;
				case WEST:  return Direction.EAST;
			}
			return current;
		}
	},
	SOUTH {
		@Override
		Direction transtateDirection(Direction current) {
			switch(current){
				case NORTH: return Direction.NORTH;
				case SOUTH: return Direction.SOUTH;
				case EAST:  return Direction.WEST;
				case WEST:  return Direction.EAST;
			}
			return current;
		}
	};
	
	/**
	 * Returns the next rotation
	 */
	public Rotation next(){
		return values()[(ordinal() +1) % values().length];
	}
	
	/**
	 * Return the correct direction for the this direction.
	 */
	abstract Direction transtateDirection(Direction current);
	
}