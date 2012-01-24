package view;

import common.Location;


public final class MapSettings {

	// tileDiagonal * zoom should be whole number 
	// Example value  when using zoom 0,6 0.8 1.0 and 1.2 are
	// 60  80 100
	
    public static int tileDiagonal = (int) 80;
    public static int tileHeight = (int) 20;
    public static int rotation = 0;
    public static float pitch = .5f;
    public static float zoom = 1.0f;
    public static Location drawStart = new Location(100, 100);

    public static void restoreDefaults() {
        tileDiagonal = 80;
        tileHeight = 20;
        rotation = 0;
        pitch = .5f;
        zoom = 1.0f;
        drawStart = new Location(300, 100);
    }
}
