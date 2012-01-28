package common;



/**
 * @author Bilal Hussain
 */
public class ProxyLocationInfo extends LocationInfo {

	
	public ProxyLocationInfo(int x, int y, int minDistance) {
		this(x,y,minDistance,null);
	}
	
	public ProxyLocationInfo(int x, int y, int minDistance, LocationInfo previous) {
		super(x, y, minDistance, previous);
	}

	public ProxyLocationInfo(LocationInfo l){
		super(l.getX(), l.getY(), l.getMinDistance(), l.getPrevious(),l.getDirection());
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
	
	public static void main(String[] args) {
		
		LocationInfo a = new LocationInfo(1, 1, 2, null);
		Location b     = new Location(1, 1);
		LocationInfo c = new ProxyLocationInfo(a);
		
		System.out.println(a.equals(b));
		System.out.println(a.equals(c));
		
		System.out.println();
		System.out.println(b.equals(a));
		System.out.println(b.equals(c));
		
		System.out.println();
		System.out.println(c.equals(a));
		System.out.println(c.equals(b));
	}
	
	
}
