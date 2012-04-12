package common;

import common.enums.Direction;
import common.interfaces.ILocation;

/**
 * Stores infomation about a tile
 * 
 * @author Bilal Hussain
 */
public class LocationInfo implements ILocation {
	/** The x coordinate */
	public int x;
	/** The y coordinate */
	public int y;
	
	private int minDistance;
	private LocationInfo previous;
	private Direction nextDirection;

	public LocationInfo(int x, int y, int minDistance) {
		this(x, y, minDistance, null);
	}

	public LocationInfo(int x, int y, int minDistance, LocationInfo previous) {
		this(x, y, minDistance, previous, Direction.STILL);
	}

	public LocationInfo(int x, int y, int minDistance, LocationInfo previous, Direction direction) {
		this.x = x;
		this.y = y;
		this.minDistance = minDistance;
		this.previous = previous;
		this.nextDirection = direction;
	}

	@Override
	public String toString() {
		return previous != null ?
				String.format("(%s,%s) %2s <%s,%s> %s",
						x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance, previous.x, previous.y, nextDirection)
				: String.format("(%s,%s) %2s null  %s",
						x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance, nextDirection);
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	public void setPrevious(LocationInfo previous) {
		this.previous = previous;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public LocationInfo getPrevious() {
		return previous;
	}

	public Direction getDirection() {
		return nextDirection;
	}

	public void setNextDirection(Direction nextDirection) {
		this.nextDirection = nextDirection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + minDistance;
		result = prime * result + ((previous == null) ? 0 : previous.x);
		result = prime * result + ((previous == null) ? 0 : previous.y);
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof LocationInfo)) return false;
		LocationInfo other = (LocationInfo) obj;
		if (minDistance != other.minDistance) return false;
		if (previous == null) {
			if (other.previous != null) return false;
		} else {
			if (other.x != x || other.y != y) return false;
		}
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}

}