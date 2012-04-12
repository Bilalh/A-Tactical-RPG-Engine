package common;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.interfaces.ILocation;

// TODO: Auto-generated Javadoc
/**
 * Point representing a location in (x, y). 
 * java.awt.Point was not used so that the model does not have a dependency on awt.
 * 
 * Nearly method return this Location which allows method chaining
 * 
 * @author Bilal Hussain
 */
@XStreamAlias("location")
public class Location implements Serializable, ILocation {
	private static final long serialVersionUID = -5276940640259749850L;

	/** The x coordinate */
	public int x;
	/** The y coordinate */
	public int y;

	/**
	 * Instantiates a new location.
	 */
	public Location() {
		this(0, 0);
	}

	/**
	 * Instantiates a new location with a the data of a another point
	 *
	 * @param p the other point
	 */
	public Location(ILocation p) {
		this(p.getX(), p.getY());
	}

	/**
	 * Instantiates a new location.
	 *
	 * @param x the x to use
	 * @param y the y to use
	 */
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * returns a copy of this location.
	 */
	public Location copy() {
		return new Location(this);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public Location setLocation(ILocation p) {
		return setLocation(p.getX(), p.getY());
	}

	public Location setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Translate the location by specifed values
	 *
	 * @param dx the x to move by.
	 * @param dy the y to move by.
	 * @return This Location for chaining.
	 */
	public Location translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}

	/**
	 * Adds the d to x and y 
	 * @return This Location for chaining.
	 */
	public Location add(int d) {
		this.x += d;
		this.y += d;
		return this;
	}
	
	/**
	 * Multiplys x and y by m
	 * @return This Location for chaining.
	 */
	public Location mult(int m) {
		this.x *= m;
		this.y *= m;
		return this;
	}

	/**
	 * Subtracts l from this point
	 * @return This Location for chaining.
	 */
	public Location sub(ILocation l){
		this.x -= l.getX();
		this.y -= l.getY();
		return this;
	}
	
	/**
	 * Negate the point
	 * @return This Location for chaining.
	 */
	public Location negate(){
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	/**
	 * Peforms the absolute value on x and y
	 */
	public Location abs(){
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	
	/**
	 * Limit the values of this point, return true if the location was modifed.
	 */
	public Location limitLower(int limitX, int limitY) {
		if (x < limitX) x = limitX;
		if (y < limitY) y = limitY;
		return this;
	}

	/**
	 * Limit the values of this point, return true if the location was modifed.
	 */
	public Location limitUpper(int limitX, int limitY) {
		if (x > limitX) x = limitX;
		if (y > limitY) y = limitY;
		return this;
	}

	/**
	 * Limit the values of this point, return true if the location was modifed.
	 */
	public boolean checkLower(int limitX, int limitY) {
		boolean b = false;
		if (x < limitX) x = limitX;
		else            b = true;
		
		if (y < limitY) y = limitY;
		else            b = true;
		
		return b;
	}
	
	/**
	 * Check upper.
	 *
	 * @param limitX the limit x
	 * @param limitY the limit y
	 * @return true, if successful
	 */
	public boolean checkUpper(int limitX, int limitY) {
		boolean b = false;
		if (x > limitX) x = limitX;
		else            b = true;
		
		if (y > limitY) y = limitY;
		else            b = true;
		
		return b;
	}
	
	/**
	 * True if p is next to this location.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean adjacent(ILocation p) {
		return (this.x == p.getX() && Math.abs(this.y - p.getY()) <= 1)
		    || (this.y == p.getY() && Math.abs(this.x - p.getX()) <= 1);
	}

	/**
	 * Distance from this location.
	 *
	 * @param b the b
	 * @return the double
	 */
	public double distance(ILocation b) {
		return distance(b, this);
	}

	/**
	 * Distance from a to b.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public static double distance(ILocation a, ILocation b) {
		int px = b.getX() - a.getX();
		int py = b.getY() - a.getY();
		return Math.sqrt(px * px + py * py);
	}

	@Override
	public String toString() {
		return String.format("L(%s,%s)", x, y);
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
		if (!(obj instanceof ILocation)) return false;
		ILocation other = (ILocation) obj;
		if (x != other.getX()) return false;
		if (y != other.getY()) return false;
		return true;
	}

}