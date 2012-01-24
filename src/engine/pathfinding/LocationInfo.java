package engine.pathfinding;

import common.ILocation;


// Stores infomation about a tile
public class LocationInfo implements ILocation {
	final int x;
	final int y;
	int minDistance;
	LocationInfo previous;
	
	/** @category Generated Constructor */
	public LocationInfo(int x, int y, int minDistance) {
		this.x = x;
		this.y = y;
		this.minDistance = minDistance;
	}

	/** @category Generated Constructor */
	public LocationInfo(int x, int y, int minDistance, LocationInfo previous) {
		this.x = x;
		this.y = y;
		this.minDistance = minDistance;
		this.previous = previous;
	}

	@Override
	public String toString() {
		return previous != null ?
				String.format("{%s,%s} %s <%s,%s>",
						x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance, previous.x, previous.y)
				: String.format("{%s,%s} %s, null",
						x, y, minDistance == Integer.MAX_VALUE ? "∞" : minDistance);
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
		} else{
			if (other.x != x  || other.y != y) return false;
		}
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}
	
	/** @category Generated Setter */
	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	/** @category Generated Setter */
	public void setPrevious(LocationInfo previous) {
		this.previous = previous;
	}

	/** @category Generated Getter */
	@Override
	public int getX() {
		return x;
	}

	/** @category Generated Getter */
	@Override
	public int getY() {
		return y;
	}

	/** @category Generated Getter */
	public int getMinDistance() {
		return minDistance;
	}

	/** @category Generated Getter */
	public LocationInfo getPrevious() {
		return previous;
	}

}