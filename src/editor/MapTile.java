package editor;


import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Arrays;

import view.MapSettings;
import static editor.MapTile.Orientation.*;

public class MapTile {
    		
    static enum Orientation{
    	NORMAL, UP_TO_NORTH, UP_TO_EAST, UP_TO_SOUTH, UP_TO_WEST, EMPTY
    }
    		
    // Colors for drawing tiles
    private static final Color TOP_COLOR = Color.DARK_GRAY;
    private static final Color RIGHT_COLOR = Color.LIGHT_GRAY;
    private static final Color LEFT_COLOR = Color.GRAY;
    private static final Color SELECTED_COLOR = Color.BLUE;
    
    // Tile Variables
    private Orientation orientation;
    private float height;
    private float startHeight;
    private float endHeight;
    private boolean selected;
    private Color myColor;
    private Polygon top;
    private Point fieldLocation;

    /**
     * Constructor
     * @param orientation The orientation of this tile
     * @param startHeight The lower height of the tile (If slanted)
     * @param endHeight The upper hieght of the tile (If slanted)
     * @category Constructor
     */
    public MapTile(Orientation orientation, float startHeight, float endHeight) {
        this.orientation = orientation;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.height = (startHeight + endHeight) / 2;
        this.selected = false;
        this.fieldLocation = new Point();
        this.myColor = TOP_COLOR;
    }

    /** @category Constructor */
    public MapTile(MapTile tile) {
        this.orientation = tile.orientation;
        this.startHeight = tile.startHeight;
        this.endHeight = tile.endHeight;
        this.height = tile.height;
        this.selected = tile.selected;
        this.fieldLocation = tile.fieldLocation;
        this.myColor = tile.myColor;
    }

    /**Check to see if this tile was clicked on.
     * This method does not set the selected tile. That is handled in the Map class
     * @param click Location of the mouse click
     */
    public boolean wasClickedOn(Point click) {
        return top.contains(click);
    }

    /**
     * Decide what color to paint the top of this tile
     * (Called from Map class)
     */
    public void determineColor() {
        if (isSelected()) {
            setColor(MapTile.SELECTED_COLOR);
            return;
        } else {
            setColor(MapTile.TOP_COLOR);
        }
    }

    public Point calculateCentrePoint(Point p){
//      final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
       final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
   	return new Point(
   			p.x, 
   			p.y + vertical/2 - finalHeight );
   }
    
    public Point calculateCentrePoint(int x, int y){
//       final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
        final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
    	return new Point(
    			x, 
    			y + vertical/2 - finalHeight );
    }
    
    /**
     * Draw this MapTile object
     * @param x x-Location to draw tile
     * @param y y-Location to draw tile
     * @param g Graphics object to draw with
     * @category drawing
     */
    public void draw(int x, int y, Graphics g) {
        switch (orientation) {
            // Drawing the standard tile (Not Slanted)
            case NORMAL:
                drawNormal(x, y, g);
                break;
            // Use the same drawing method for the North/South slants
            case UP_TO_NORTH:
            case UP_TO_SOUTH:
                drawNorthSouth(x, y, g);
                break;
            // Use the same drawing method for the East/West slants
            case UP_TO_EAST:
            case UP_TO_WEST:
                drawEastWest(x, y, g);
                break;
            // Ignore Empty tiles
            case EMPTY:
                break;
            default:
                //System.out.println("That orientation doesn't exist!");
        }
    }

    /**
     * Draw a Standard Tile.  Standard Tiles do not have a slant to them.
     * Note that the drawing begins at (x,y - MapSettings.tileHeight*height).
     * @param x X-location of the tile.
     * @param y Y-location of the tile.
     * @param g The Graphcis object with which to draw.
     * @category drawing
     */
    public void drawNormal(int x, int y, Graphics g) {
        determineColor();
        
        
        int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
        int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
        int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
        //System.out.printf("(%d,%d) finalHeight:%3d hoz:%3d vet:%3d\n",x,y,  finalHeight, horizontal, vertical);
        
        Color oldColor = g.getColor();
        g.setColor(myColor); // Draw the top of the tile
        g.fillPolygon(top = new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x, 
        		x - horizontal / 2},
                new int[]{
        		y - finalHeight, 
        		y + vertical / 2 - finalHeight, 
        		y + vertical - finalHeight, 
        		y + vertical / 2 - finalHeight}
        , 4));
        //System.out.println("x:" +Arrays.toString(top.xpoints));
        //System.out.println("y:" + Arrays.toString(top.ypoints));

        g.setColor(RIGHT_COLOR); // Draw the right side
        g.fillPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - finalHeight + vertical, 
        		y - finalHeight + vertical / 2,
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(LEFT_COLOR); // Draw the left side
        g.fillPolygon(new Polygon(new int[]{
        		x, 
        		x - horizontal / 2, 
        		x - horizontal / 2, 
        		x},
                new int[]{
        		y - finalHeight + vertical, 
        		y - finalHeight + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));

        g.setColor(Color.BLACK); // Outline the top
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2,
        		x, 
        		x - horizontal / 2},
                new int[]{
        		y - finalHeight, 
        		y + vertical / 2 - finalHeight, 
        		y + vertical - finalHeight, 
        		y + vertical / 2 - finalHeight}
        , 4));
        // Outline the right side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - finalHeight + vertical, 
        		y - finalHeight + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        // Outline the left side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x - horizontal / 2, 
        		x - horizontal / 2, 
        		x},
                new int[]{
        		y - finalHeight + vertical, 
        		y - finalHeight + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(Color.RED);
        g.setColor(oldColor);
    }

    /**
     * Draw a tile that slants to the north or the south.  The methods for drawing
     * are very similar, so they can be merged into one method.
     * Note that the drawing begins at (x,y - MapSettings.tileHeight*height).
     * @param x X-location of the tile.
     * @param y Y-location of the tile.
     * @param g The Graphcis object with which to draw.
     * @category drawing
     */
    public void drawNorthSouth(int x, int y, Graphics g) {
        determineColor();

        int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
        int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
        int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
        int HEIGHT1 = orientation == Orientation.UP_TO_NORTH ? (int) (finalHeight * startHeight) : (int) (finalHeight * endHeight);
        int HEIGHT2 = orientation == UP_TO_NORTH ? (int) (finalHeight * endHeight) : (int) (finalHeight * startHeight);

        Color oldColor = g.getColor();
        g.setColor(myColor); // Draw the top of the tile
        g.fillPolygon(top = new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x, x - horizontal / 2},
                new int[]{
        		y - HEIGHT2, 
        		y - HEIGHT2 + vertical / 2,
        		y - HEIGHT1 + vertical,
        		y - HEIGHT1 + vertical / 2}
        , 4));
        g.setColor(RIGHT_COLOR); // Draw the right side
        g.fillPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - HEIGHT1 + vertical, 
        		y - HEIGHT2 + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(LEFT_COLOR); // Draw the left side
        g.fillPolygon(new Polygon(new int[]{
        		x, 
        		x - horizontal / 2, 
        		x - horizontal / 2, 
        		x},
                new int[]{
        		y - HEIGHT1 + vertical, 
        		y - HEIGHT1 + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(Color.BLACK); // Outline the top of the tile
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x, 
        		x - horizontal / 2},
                new int[]{
        		y - HEIGHT2, 
        		y - HEIGHT2 + vertical / 2, 
        		y - HEIGHT1 + vertical, 
        		y - HEIGHT1 + vertical / 2}
        , 4));
        // Outline the right side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - HEIGHT1 + vertical, 
        		y - HEIGHT2 + vertical / 2,
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        // Outline the left side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x - horizontal / 2, 
        		x - horizontal / 2, 
        		x},
                new int[]{y - HEIGHT1 + vertical, 
        		y - HEIGHT1 + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(oldColor);
    }

    /**
     * Draw a tile that slants to the east or west.  The methods for drawing
     * are very similar, so they can be merged into one method.
     * Note that the drawing begins at (x,y - MapSettings.tileHeight*height).
     * @param x X-location of the tile.
     * @param y Y-location of the tile.
     * @param g The Graphcis object with which to draw.
     * @category drawing
     */
    public void drawEastWest(int x, int y, Graphics g) {
        determineColor();

        int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
        int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
        int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
        int HEIGHT1 = orientation == UP_TO_EAST ? (int) (finalHeight * startHeight) : (int) (finalHeight * endHeight);
        int HEIGHT2 = orientation == UP_TO_EAST ? (int) (finalHeight * endHeight) : (int) (finalHeight * startHeight);

        Color oldColor = g.getColor();
        g.setColor(myColor); // Draw the top of the tile
        g.fillPolygon(top = new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x, 
        		x - horizontal / 2},
                new int[]{
        		y - HEIGHT1, 
        		y - HEIGHT2 + vertical / 2, 
        		y - HEIGHT2 + vertical,
        		y - HEIGHT1 + vertical / 2}
        , 4));
        g.setColor(RIGHT_COLOR); // Draw the right side
        g.fillPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - HEIGHT2 + vertical, 
        		y - HEIGHT2 + vertical / 2,
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(LEFT_COLOR); // Draw the left side
        g.fillPolygon(new Polygon(new int[]{
        		x,
        		x - horizontal / 2,
        		x - horizontal / 2,
        		x},
                new int[]{
        		y - HEIGHT2 + vertical,
        		y - HEIGHT1 + vertical / 2,
        		y + vertical / 2,
        		y + vertical}
        , 4));

        g.setColor(Color.BLACK); // Outline the top of the tile
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x, 
        		x - horizontal / 2},
                new int[]{
        		y - HEIGHT1, 
        		y - HEIGHT2 + vertical / 2, 
        		y - HEIGHT2 + vertical, 
        		y - HEIGHT1 + vertical / 2}
        , 4));
        // Outline the right side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x + horizontal / 2, 
        		x + horizontal / 2, 
        		x},
                new int[]{
        		y - HEIGHT2 + vertical,
        		y - HEIGHT2 + vertical / 2, 
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        // Outline the left side
        g.drawPolygon(new Polygon(new int[]{
        		x, 
        		x - horizontal / 2, 
        		x - horizontal / 2,
        		x},
                new int[]{
        		y - HEIGHT2 + vertical, 
        		y - HEIGHT1 + vertical / 2,
        		y + vertical / 2, 
        		y + vertical}
        , 4));
        g.setColor(oldColor);
    }

    @Override
    public MapTile clone() {
        return new MapTile(orientation, startHeight, endHeight);
    }

    
    public void setEndHeight(float endHeight) {
        //System.out.println("Setting End Height:\n" + this.startHeight + ", " + this.endHeight + ", " + this.height);
        this.endHeight = endHeight;
        this.height = (startHeight + endHeight) / 2;
        //System.out.println(this.startHeight + ", " + this.endHeight + ", " + this.height);
    }
    
    public void setStartHeight(float startHeight) {
        //System.out.println("Setting Start Height:\n" + this.startHeight + ", " + this.endHeight + ", " + this.height);
        this.startHeight = startHeight;
        this.height = (startHeight + endHeight) / 2;
        //System.out.println(this.startHeight + ", " + this.endHeight + ", " + this.height);
    }

	/** @category Generated */
	public Orientation getOrientation() {
		return orientation;
	}

	/** @category Generated */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/** @category Generated */
	public float getHeight() {
		return height;
	}

	/** @category Generated */
	public void setHeight(float height) {
		this.height = height;
	}

	/** @category Generated */
	public boolean isSelected() {
		return selected;
	}

	/** @category Generated */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/** @category Generated */
	public Color getColor() {
		return myColor;
	}

	/** @category Generated */
	public void setColor(Color myColor) {
		this.myColor = myColor;
	}

	/** @category Generated */
	public Point getFieldLocation() {
		return fieldLocation;
	}

	/** @category Generated */
	public void setFieldLocation(Point fieldLocation) {
		this.fieldLocation = fieldLocation;
	}

	/** @category Generated */
	public float getStartHeight() {
		return startHeight;
	}

	/** @category Generated */
	public float getEndHeight() {
		return endHeight;
	}
	
}


