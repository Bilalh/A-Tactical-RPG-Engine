package engine.skills;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.Location;

/**
 * Provides methods to creates ranges around a location and for bounding checking. 
 * Also setups the unique id.  
 * @author Bilal Hussain
 */
public class AbstractUnitObject {

	protected String name;
	@XStreamAsAttribute
	protected final UUID uuid;

	public AbstractUnitObject() {
		uuid = UUID.randomUUID();
		name = uuid.toString();
	}

	/**
	 * Makes a diamend shaped range around a specifed point.
	 * 
	 * <pre>
	 *    +
	 *   +++
	 *  ++S++
	 *   +++
	 *    +
	 * </pre>
	 * 
	 * Above shows the result for a range of 2 around the start(S)
	 * 
	 * The result inculdes contain the starting point. 
	 * 
	 * @param  start  The start location.
	 * @param  width  The width of the map.
	 * @param  height The hieght of the map.
	 * @param  range  The number squares around the starting point 
	 * 
	 * @return A Hashset containing the points 
	 */
	public HashSet<Location> makeRange(Location start, int width, int height, int range) {
		HashSet<Location> set = new HashSet<Location>();
		
		for (int i = range; i > 0; i--) {
			for (int j = -1; j <= 1; j += 2) {
				final int diff = start.y + j * (range - i);
				if (diff >= height || diff < 0) continue;
	
				Location m = start.copy().translate(0, j * (range - i));
				set.add(m);
				for (int k = 1; k <= i; k++) {
					if (m.x + k < width) set.add(m.copy().translate(k, 0));
					if (m.x - k >= 0)    set.add(m.copy().translate(-k, 0));
				}
			}
		}
	
		if (start.y + range < height) set.add(start.copy().translate(0, range));
		if (start.y - range >= 0)     set.add(start.copy().translate(0, -range));
		return set;
	}

	/**
	 * Does bound checking. Removes any locations that are out the bounds from the collection.
	 * @return The collection with any location that out of bounds removed.
	 */
	public <E extends Collection<Location>> E boundCheck(E collection, int width, int height) {
		for (Iterator<Location> it = collection.iterator(); it.hasNext();) {
			Location t = it.next();
			if (t.x < 0 && t.x >= width && t.y < 0 && t.y >= height) {
				it.remove();
			}
		}
		return collection;
	}

	/** @category Generated */
	public UUID getUuid() {
		return uuid;
	}

	/** @category Generated */
	public void setName(String name) {
		this.name = name;
	}

	/** @category Generated */
	public String getName() {
		return name;
	}

}