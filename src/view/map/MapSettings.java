package view.map;



import common.Location;
import common.interfaces.ILocation;


public final class MapSettings {

	// tileDiagonal * zoom should be whole number 
	// Example value  when using zoom 0,6 0.8 1.0 and 1.2 are
	// 60  80 100
	
    public static int tileDiagonal = (int) 60;
    public static int tileHeight = (int) 10;
    public static float pitch = .5f;
    public static float zoom = 1.0f;
}
