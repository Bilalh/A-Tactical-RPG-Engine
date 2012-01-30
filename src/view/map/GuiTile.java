package view.map;

import static view.map.GuiTile.Orientation.*;
import static common.enums.ImageType.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import common.enums.ImageType;
import common.gui.Sprite;
import common.gui.SpriteManager;
import common.interfaces.IMapUnit;

import javax.swing.*;

import view.AnimatedUnit;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

import common.Location;
import common.Location;
import common.Location;

import common.Location;
import config.xml.TileImageData;

public class GuiTile {

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
	
	private Polygon top; // For mouse testing 
	private Location fieldLocation;
	
	
	private TileState state;
	private boolean selected = false;

	// The Tiles image 
	private Image tileImage;
	private ImageType type;
	
	private AnimatedUnit unit;
	
	// debuging
	private int cost;
	
	// testing
	BufferedImage iGrass = SpriteManager.instance().getSprite("assets/gui/grass32.jpg").getImage();
//	BufferedImage iGrass = SpriteManager.instance().getSprite("assets/gui/tileeesmall.png").getImage();
	Rectangle2D rGrass = new Rectangle2D.Double(0, 0, iGrass.getWidth(null), iGrass.getHeight(null));
	TexturePaint tGrass = new TexturePaint(iGrass, rGrass);

	// testing
	static Image iWall = SpriteManager.instance().getSprite("assets/gui/wallb16.jpg").getImage();
	static Rectangle2D rWall = new Rectangle2D.Double(0, 0, iWall.getWidth(null),iWall.getHeight(null));
	static TexturePaint tWall = new TexturePaint((BufferedImage) iWall, rWall);
	
	
	public GuiTile(Orientation orientation, float startHeight, float endHeight, 
			int x, int y, String filename, ImageType type ) {
		this.fieldLocation = new Location(x, y);
		this.orientation   = orientation;
		
		this.startHeight = startHeight;
		this.endHeight   = endHeight;
		
		this.height = (startHeight + endHeight) / 2;
		this.type   = type;
		this.state  = TileState.NONE;

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
		
		
		Sprite image = SpriteManager.instance().getSprite("Resources/" + filename);
		int mheight =  Math.round((MapSettings.tileDiagonal/2f));
		tileImage    = image.getImage().getScaledInstance(MapSettings.tileDiagonal+1,mheight+1, Image.SCALE_SMOOTH);
	}
	
	public boolean wasClickedOn(Point click) {
		return top != null && top.contains(click);
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
			case UP_TO_NORTH:
			case UP_TO_SOUTH:
				drawNorthSouth(x, y, g);
				break;
			case UP_TO_EAST:
			case UP_TO_WEST:
				drawEastWest(x, y, g, drawLeftSide, drawRightSide);
				break;
			case EMY:
				break;
			default:
			assert(false);
		}
	}

	Color lineColor = Color.BLACK;
	public void drawEastWest(int x, int y, Graphics _g, boolean drawLeftSide, boolean drawRightSide) {
		Graphics2D g = (Graphics2D) _g;
		final float finalHeight = (MapSettings.tileHeight * MapSettings.zoom);
		final float horizontal  = (MapSettings.tileDiagonal * MapSettings.zoom);
		final float vertical    = (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		
		final float h1 = orientation == UP_TO_EAST ? (finalHeight * startHeight) : (finalHeight * endHeight);
		final float h2 = orientation == UP_TO_EAST ? (finalHeight * endHeight)   : (finalHeight * startHeight);

		Color oldColor = g.getColor();
		Paint old = g.getPaint();

//		final int x_hor_div_2         = (int) (x + horizontal / 2);
//		final int neg_x_hor_div_2     = (int) (x - horizontal / 2);
//		final int y_vet               = (int) (y + vertical);
//		final int y_vet_div_2         = (int) (y + vertical / 2);
//		final int neg_y_h1_vet_div_2  = (int) (y - h1 + vertical / 2);
//		final int neg_y_h2_vet_div_2  = (int) (y - h2 + vertical / 2);
//		final int neg_y_h2_vet        = (int) (y - h2 + vertical);
		final int x_hor_div_2         =Math.round(x + horizontal / 2);
		final int neg_x_hor_div_2     =Math.round(x - horizontal / 2);
		final int y_vet               =Math.round(y + vertical);
		final int y_vet_div_2         =Math.round(y + vertical / 2);
		final int neg_y_h1_vet_div_2  =Math.round(y - h1 + vertical / 2);
		final int neg_y_h2_vet_div_2  =Math.round(y - h2 + vertical / 2);
		final int neg_y_h2_vet        =Math.round(y - h2 + vertical);
		
		top = new Polygon(new int[] {
				x,
				x_hor_div_2,
				x,
				neg_x_hor_div_2},
				new int[] {
						(int) (y - h1),
						neg_y_h2_vet_div_2,
						neg_y_h2_vet,
						neg_y_h1_vet_div_2 }, 4);
		if (type == TEXTURED) {
			g.setPaint(tGrass);
			g.fillPolygon(top);
			g.setPaint(old);
		} else {
			int f = Math.round(x - horizontal / 2f);
			g.drawImage(tileImage,  f, (int) (y - h2), null);
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
					x_hor_div_2,
					x_hor_div_2,
					x },
					new int[] {
							neg_y_h2_vet,
							neg_y_h2_vet_div_2,
							y_vet_div_2,
							y_vet }
					, 4);

			old = g.getPaint();
			g.setPaint(tWall);
			g.fillPolygon(poly);
			g.setPaint(old);

//			g.setColor(lineColor); // Outline
//			g.drawPolygon(new Polygon(new int[] {
//					x,
//					x_hor_div_2,
//					x_hor_div_2,
//					x },
//					new int[] {
//							neg_y_h2_vet,
//							neg_y_h2_vet_div_2,
//							y_vet_div_2,
//							y_vet }
//					, 4));
		}

		if (drawLeftSide) {
			Polygon poly = new Polygon(new int[] {
					x,
					neg_x_hor_div_2,
					neg_x_hor_div_2,
					x },
					new int[] {
							neg_y_h2_vet,
							neg_y_h1_vet_div_2,
							y_vet_div_2,
							y_vet }
					, 4);
			old = g.getPaint();
			g.setPaint(tWall);
			g.fillPolygon(poly);
			g.setPaint(old);

//			g.setColor(lineColor); // Outline
//			g.drawPolygon(new Polygon(new int[] {
//					x,
//					neg_x_hor_div_2,
//					neg_x_hor_div_2,
//					x },
//					new int[] {
//							neg_y_h2_vet,
//							neg_y_h1_vet_div_2,
//							y_vet_div_2,
//							y_vet }
//					, 4));

		}
//
		g.setColor(lineColor); // Outline the top of the tile
		g.drawPolygon(new Polygon(new int[] {
				x,
				x_hor_div_2,
				x,
				neg_x_hor_div_2 },
				new int[] {
						(int) (y - h1),
						neg_y_h2_vet_div_2,
						neg_y_h2_vet,
						neg_y_h1_vet_div_2 }
				, 4));
//		
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
	public AnimatedUnit getUnit() {
		return unit;
	}

	public AnimatedUnit removeUnit(){
		AnimatedUnit temp = unit;
		unit = null;
		return temp;
	}
	
	/** @category Generated */
	public void setUnit(AnimatedUnit unit) {
		this.unit = unit;
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
	public Location getFieldLocation() {
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

	/** @category Generated */
	@Override
	public String toString() {
		return String.format("MapTile [way=%s, height=%s, p=%s, cost=%s]",
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