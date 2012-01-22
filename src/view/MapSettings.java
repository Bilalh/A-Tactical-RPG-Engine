package view;

import java.awt.Point;


public final class MapSettings {

    // Map Settings
    public static int tileDiagonal = 75;
    public static int tileHeight = 15;
    public static int rotation = 0;
    public static float pitch = .5f;
    public static float zoom = 1.0f;
    public static Point drawStart = new Point(100, 100);

    public static void restoreDefaults() {
        tileDiagonal = 80;
        tileHeight = 20;
        rotation = 0;
        pitch = .5f;
        zoom = 1.0f;
        drawStart = new Point(300, 100);
    }
}
