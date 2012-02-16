package view.map;

import common.enums.Direction;

/**
 * Postion of 0,0 on the map 
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
				case STILL: return Direction.STILL;
			}
			return current;
		}
	},
	EAST {
		@Override
		Direction transtateDirection(Direction current) {
			return current;
		}
	},
	SOUTH {
		@Override
		Direction transtateDirection(Direction current) {
			return current;
		}
	};
	
	/**
	 * Returns the next rotation
	 */
	public Rotation next(){
		return values()[(ordinal() +1) % values().length];
	}
	
	abstract Direction transtateDirection(Direction current);
	
}