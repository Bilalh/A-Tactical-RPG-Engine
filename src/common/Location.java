package common;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.interfaces.ILocation;

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

	public int x;
	public int y;

	public Location() {
		this(0, 0);
	}

	public Location(ILocation p) {
		this(p.getX(), p.getY());
	}

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

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

	/**
	 * @return This Point for chaining.
	 */
	public Location setLocation(ILocation p) {
		return setLocation(p.getX(), p.getY());
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
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
	public Location add(int d) {
		this.x += d;
		this.y += d;
		return this;
	}
	
	/**
	 * @return This Point for chaining.
	 */
	public Location mult(int m) {
		this.x *= m;
		this.y *= m;
		return this;
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location sub(ILocation l){
		this.x -= l.getX();
		this.y -= l.getY();
		return this;
	}
	
	/**
	 * @return This Point for chaining.
	 */
	public Location negate(){
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}
	
	/**
	 * @return This Point for chaining.
	 */
	public Location limitLower(int limitX, int limitY) {
		if (x < limitX) x = limitX;
		if (y < limitY) y = limitY;
		return this;
	}

	/**
	 * @return This Point for chaining.
	 */
	public Location limitUpper(int limitX, int limitY) {
		if (x > limitX) x = limitX;
		if (y > limitY) y = limitY;
		return this;
	}

	public boolean checkLower(int limitX, int limitY) {
		boolean b = false;
		if (x < limitX) x = limitX;
		else            b = true;
		
		if (y < limitY) y = limitY;
		else            b = true;
		
		return b;
	}
	
	public boolean checkUpper(int limitX, int limitY) {
		boolean b = false;
		if (x > limitX) x = limitX;
		else            b = true;
		
		if (y > limitY) y = limitY;
		else            b = true;
		
		return b;
	}
	
	/**
	 * True if p is next to this location
	 */
	public boolean adjacent(ILocation p) {
		return (this.x == p.getX() && Math.abs(this.y - p.getY()) <= 1)
		    || (this.y == p.getY() && Math.abs(this.x - p.getX()) <= 1);
	}

	/**
	 * Distance from this location
	 */
	public double distance(ILocation b) {
		return distance(b, this);
	}

	/**
	 * Distance from a to b
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