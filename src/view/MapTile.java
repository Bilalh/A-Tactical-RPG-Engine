package view;

import static view.MapTile.Orientation.UP_TO_EAST;
import static view.MapTile.Orientation.UP_TO_NORTH;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import common.gui.Sprite;
import common.gui.SpriteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class MapTile {

	static enum Orientation {
		NORMAL, UP_TO_NORTH, UP_TO_EAST, UP_TO_SOUTH, UP_TO_WEST, EMY
	}

	static enum TileState {
		SELECTED(Color.ORANGE), MOVEMENT_RANGE(Color.BLUE), NONE(Color.GREEN);
		public final Color colour;

		TileState(Color c) {
			this.colour = c;
		}
	}

	// Tile Variables
	private Orientation orientation;
	private float height;
	private float startHeight;
	private float endHeight;
	private Point fieldLocation;
	private TileState state;

	private Polygon topReal;
	Polygon top, left, right;

	private boolean selected = false;

	// debuging
	private int cost;
	BufferedImage iGrass = SpriteManager.instance().getSprite("assets/gui/metal.png").getImage();
	Rectangle2D rGrass = new Rectangle2D.Double(0, 0, iGrass.getWidth(null), iGrass.getHeight(null));
	TexturePaint tGrass = new TexturePaint(iGrass, rGrass);

	static Image iWall = SpriteManager.instance().getSprite("assets/gui/wallb16.jpg").getImage();
	static Rectangle2D rWall = new Rectangle2D.Double(0, 0, iWall.getWidth(null),
			iWall.getHeight(null));
	static TexturePaint tWall = new TexturePaint((BufferedImage) iWall, rWall);
	
	// True if textured  otherwise a tilemap is used.
	boolean textured = true;
	Sprite image = SpriteManager.instance().getSprite("assets/gui/tile2.png");
	
	/**
	 * @param orientation
	 *            The orientation of this tile
	 * @param startHeight
	 *            The lower height of the tile (If slanted)
	 * @param endHeight
	 *            The upper hieght of the tile (If slanted)
	 * @category Constructor
	 */
	public MapTile(Orientation orientation, float startHeight, float endHeight, int x, int y) {
		this.orientation = orientation;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.height = (startHeight + endHeight) / 2;
		this.fieldLocation = new Point();
		this.fieldLocation = new Point(x, y);

		final int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		final int h1 = orientation == UP_TO_EAST ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		final int h2 = orientation == UP_TO_EAST ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);

		top = new Polygon(new int[] {
				0,
				horizontal / 2,
				0,
				0 - horizontal / 2 },
				new int[] {
						0 - h1,
						0 - h2 + vertical / 2,
						0 - h2 + vertical,
						0 - h1 + vertical / 2 }, 4);
		state = TileState.NONE;
	}

	public boolean wasClickedOn(Point click) {
		return topReal != null && topReal.contains(click);
	}

	public Point calculateCentrePoint(Point p) {
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
		return new Point(
				p.x,
				p.y + vertical / 2 - finalHeight);
	}

	public Point calculateCentrePoint(int x, int y) {
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
		return new Point(
				x,
				y + vertical / 2 - finalHeight);
	}

	public void draw(int x, int y, Graphics g, boolean drawLeftSide, boolean drawRightSide) {
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
				drawEastWest(x, y, g, drawLeftSide, drawRightSide);
				break;
			// Ignore Emy tiles
			case EMY:
				break;
			default:
				// System.out.println("That orientation doesn't exist!");
		}
	}

	/**
	 * @category unused
	 */
	Polygon getpoly() {
		final int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		final int h1 = orientation == UP_TO_EAST ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		final int h2 = orientation == UP_TO_EAST ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);
		return new Polygon(new int[] {
				0,
				0 + horizontal / 2,
				0,
				0 - horizontal / 2 },
				new int[] {
						0 - h1,
						0 - h2 + vertical / 2,
						0 - h2 + vertical,
						0 - h1 + vertical / 2 }, 4);
	}

	void makePolygons(int x, int y) {
		final int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		final int h1 = orientation == UP_TO_EAST ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		final int h2 = orientation == UP_TO_EAST ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);
		top = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x,
				x - horizontal / 2 },
				new int[] {
						y - h1,
						y - h2 + vertical / 2,
						y - h2 + vertical,
						y - h1 + vertical / 2 }, 4);

		right = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x + horizontal / 2,
				x },
				new int[] {
						y - h2 + vertical,
						y - h2 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4);

		left = new Polygon(new int[] {
				x,
				x - horizontal / 2,
				x - horizontal / 2,
				x },
				new int[] {
						y - h2 + vertical,
						y - h1 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4);

	}

	public void drawEastWest(int x, int y, Graphics _g, boolean drawLeftSide, boolean drawRightSide) {
		Graphics2D g = (Graphics2D) _g;

		final int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		final int h1 = orientation == UP_TO_EAST ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		final int h2 = orientation == UP_TO_EAST ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);

		Color oldColor = g.getColor();
		Paint old = g.getPaint();

		top = topReal = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x,
				x - horizontal / 2 },
				new int[] {
						y - h1,
						y - h2 + vertical / 2,
						y - h2 + vertical,
						y - h1 + vertical / 2 }, 4);

		if (textured) {
			g.setPaint(tGrass);
			g.fillPolygon(top);
			g.setPaint(old);
		} else {
			Image i = image.getImage().getScaledInstance(MapSettings.tileDiagonal,MapSettings.tileDiagonal/2, Image.SCALE_SMOOTH);
			g.drawImage(i, x - horizontal / 2, y - h2+1, null);
		}

		if (state == TileState.MOVEMENT_RANGE || selected) {
			Composite oldC = g.getComposite();
			AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.25f);
			g.setComposite(alphaComposite);
			g.setColor(selected ? TileState.SELECTED.colour : state.colour);
			g.fillPolygon(top);
			g.setComposite(oldC);
		}

		if (drawRightSide) {
			Polygon poly = new Polygon(new int[] {
					x,
					x + horizontal / 2,
					x + horizontal / 2,
					x },
					new int[] {
							y - h2 + vertical,
							y - h2 + vertical / 2,
							y + vertical / 2,
							y + vertical }
					, 4);

			old = g.getPaint();
			g.setPaint(tWall);
			g.fillPolygon(poly);
			g.setPaint(old);

			g.setColor(Color.BLACK); // Outline
			g.drawPolygon(new Polygon(new int[] {
					x,
					x + horizontal / 2,
					x + horizontal / 2,
					x },
					new int[] {
							y - h2 + vertical,
							y - h2 + vertical / 2,
							y + vertical / 2,
							y + vertical }
					, 4));
		}

		if (drawLeftSide) {
			Polygon poly = new Polygon(new int[] {
					x,
					x - horizontal / 2,
					x - horizontal / 2,
					x },
					new int[] {
							y - h2 + vertical,
							y - h1 + vertical / 2,
							y + vertical / 2,
							y + vertical }
					, 4);
			old = g.getPaint();
			g.setPaint(tWall);
			g.fillPolygon(poly);
			g.setPaint(old);

			g.setColor(Color.BLACK); // Outline
			g.drawPolygon(new Polygon(new int[] {
					x,
					x - horizontal / 2,
					x - horizontal / 2,
					x },
					new int[] {
							y - h2 + vertical,
							y - h1 + vertical / 2,
							y + vertical / 2,
							y + vertical }
					, 4));

		}

//		g.setColor(Color.BLACK); // Outline the top of the tile
//		g.drawPolygon(new Polygon(new int[] {
//				x,
//				x + horizontal / 2,
//				x,
//				x - horizontal / 2 },
//				new int[] {
//						y - h1,
//						y - h2 + vertical / 2,
//						y - h2 + vertical,
//						y - h1 + vertical / 2 }
//				, 4));

		g.setColor(oldColor);
	}

	/**
	 * Draw a Standard Tile. Standard Tiles do not have a slant to them. Note that the drawing
	 * begins at (x,y - MapSettings.tileHeight*height).
	 * 
	 * @category unused
	 */
	public void drawNormal(int x, int y, Graphics g) {
		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom * height);
		int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		// System.out.printf("(%d,%d) finalHeight:%3d hoz:%3d vet:%3d\n",x,y, finalHeight,
		// horizontal, vertical);

		Color oldColor = g.getColor();
		g.setColor(state.colour);
		g.fillPolygon(top = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x,
				x - horizontal / 2 },
				new int[] {
						y - finalHeight,
						y + vertical / 2 - finalHeight,
						y + vertical - finalHeight,
						y + vertical / 2 - finalHeight }
				, 4));

		g.fillPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x + horizontal / 2,
				x },
				new int[] {
						y - finalHeight + vertical,
						y - finalHeight + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));

		g.fillPolygon(new Polygon(new int[] {
				x,
				x - horizontal / 2,
				x - horizontal / 2,
				x },
				new int[] {
						y - finalHeight + vertical,
						y - finalHeight + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));

		g.setColor(Color.BLACK); // Outline the top
		g.drawPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x,
				x - horizontal / 2 },
				new int[] {
						y - finalHeight,
						y + vertical / 2 - finalHeight,
						y + vertical - finalHeight,
						y + vertical / 2 - finalHeight }
				, 4));
		// Outline the right side
		g.drawPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x + horizontal / 2,
				x },
				new int[] {
						y - finalHeight + vertical,
						y - finalHeight + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));
		// Outline the left side
		g.drawPolygon(new Polygon(new int[] {
				x,
				x - horizontal / 2,
				x - horizontal / 2,
				x },
				new int[] {
						y - finalHeight + vertical,
						y - finalHeight + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));
		g.setColor(Color.RED);
		g.setColor(oldColor);
	}

	/**
	 * Draw a tile that slants to the north or the south. The methods for drawing are very similar,
	 * so they can be merged into one method.
	 * 
	 * @category unused
	 */
	public void drawNorthSouth(int x, int y, Graphics g) {

		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int HEIGHT1 = orientation == Orientation.UP_TO_NORTH ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		int HEIGHT2 = orientation == UP_TO_NORTH ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);

		Color oldColor = g.getColor();
		g.setColor(state.colour);
		g.fillPolygon(top = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x, x - horizontal / 2 },
				new int[] {
						y - HEIGHT2,
						y - HEIGHT2 + vertical / 2,
						y - HEIGHT1 + vertical,
						y - HEIGHT1 + vertical / 2 }
				, 4));

		g.fillPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x + horizontal / 2,
				x },
				new int[] {
						y - HEIGHT1 + vertical,
						y - HEIGHT2 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));

		g.fillPolygon(new Polygon(new int[] {
				x,
				x - horizontal / 2,
				x - horizontal / 2,
				x },
				new int[] {
						y - HEIGHT1 + vertical,
						y - HEIGHT1 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));
		g.setColor(Color.BLACK); // Outline the top of the tile
		g.drawPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x,
				x - horizontal / 2 },
				new int[] {
						y - HEIGHT2,
						y - HEIGHT2 + vertical / 2,
						y - HEIGHT1 + vertical,
						y - HEIGHT1 + vertical / 2 }
				, 4));
		// Outline the right side
		g.drawPolygon(new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x + horizontal / 2,
				x },
				new int[] {
						y - HEIGHT1 + vertical,
						y - HEIGHT2 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));
		// Outline the left side
		g.drawPolygon(new Polygon(new int[] {
				x,
				x - horizontal / 2,
				x - horizontal / 2,
				x },
				new int[] { y - HEIGHT1 + vertical,
						y - HEIGHT1 + vertical / 2,
						y + vertical / 2,
						y + vertical }
				, 4));
		g.setColor(oldColor);
	}

	/** @category Generated */
	public Orientation getOrientation() {
		return orientation;
	}

	/** @category Generated */
	public float getHeight() {
		return height;
	}

	/** @category Generated */
	public Point getFieldLocation() {
		return fieldLocation;
	}

	/** @category Generated */
	public float getStartHeight() {
		return startHeight;
	}

	/** @category Generated */
	public float getEndHeight() {
		return endHeight;
	}

	/** @category Generated */
	public TileState getState() {
		return state;
	}

	/** @category Generated */
	public void setState(TileState state) {
		this.state = state;
	}

	// for debuging
	/** @category Generated */
	public int getCost() {
		return cost;
	}

	/** @category Generated */
	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return String.format("MapTile [orientation=%s, height=%s, fieldLocation=%s, cost=%s]",
				orientation, height, fieldLocation, cost);
	}

	/** @category Generated Getter */
	public boolean isSelected() {
		return selected;
	}

	/** @category Generated Setter */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}