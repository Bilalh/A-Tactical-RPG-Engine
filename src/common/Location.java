package common;

import java.io.Serializable;

/**
 * Point representing a location in (x, y). 
 * java.awt.Point was not used so that the model does not have dependency on awt.
 * @author Bilal Hussain
 */
public class Location implements Serializable {

	public int x;
	public int y;

	private static final long serialVersionUID = -5276940640259749850L;

	public Location() {
		this(0, 0);
	}

	public Location(Location p) {
		this(p.x, p.y);
	}

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location limitLower(int limitX, int limitY){
		if (x < limitX) x = limitX;
		if (y < limitY) y = limitY;
		return this;
	}
	
	/**
	 * @return This Point for chaining.
	 */
	public Location limitUpper(int limitX, int limitY){
		if (x > limitX) x = limitX;
		if (y > limitY) y = limitY;
		return this;
	}
	
	public Location copy(){
		return new Location(this);
	}
	
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location setLocation(Location p) {
		return setLocation(p.x, p.y);
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[x=" + x + ",y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Location)) return false;
		Location other = (Location) obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}

}