package view.map;

import static common.enums.ImageType.*;
import static common.enums.Orientation.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import common.enums.ImageType;
import common.enums.Orientation;
import common.gui.Sprite;
import common.gui.ResourceManager;
import common.interfaces.IMapUnit;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.log4j.Logger;

import util.Logf;
import view.units.AnimatedUnit;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import common.Location;
import common.Location;
import common.Location;

import common.Location;
import config.xml.TileImageData;

public class IsoTile {
	private static final Logger log = Logger.getLogger(IsoTile.class);
	
	public static enum TileState {
		NONE(Color.BLACK), SELECTED(Color.BLACK), 
		MOVEMENT_RANGE(Color.BLUE), OTHERS_RANGE(Color.ORANGE), ATTACK_RANGE(Color.RED), ATTACK_AREA(Color.GREEN);
		public Color colour;

		TileState(Color c) {
			this.colour = c;
		}
		
		public void setColor(Color c){
			this.colour = c;
		}
		
	}

	// Tile Variables
	protected Orientation orientation;
	protected float height;
	protected float startHeight;
	protected float endHeight;

	protected Polygon top; // For mouse testing 
	protected Location fieldLocation;

	protected TileState state;
	// TODO use enumSet?
	protected boolean selected = false;

	// The Tiles image 
	protected BufferedImage tileImage;
	protected ImageType type;
	protected String name;
	
	protected AnimatedUnit unit;

	// testing
	BufferedImage iGrass = ResourceManager.instance().getSpriteFromClassPath("assets/gui/grass32.jpg").getImage();
//	BufferedImage iGrass = SpriteManager.instance().getSprite("assets/gui/testTile.png").getImage();
	Rectangle2D rGrass = new Rectangle2D.Double(0, 0, iGrass.getWidth(null), iGrass.getHeight(null));
	TexturePaint tGrass = new TexturePaint(iGrass, rGrass);

	// testing
	static Image iWall = ResourceManager.instance().getSpriteFromClassPath("assets/gui/wallb16.jpg").getImage();
	static Rectangle2D rWall = new Rectangle2D.Double(0, 0, iWall.getWidth(null),iWall.getHeight(null));
	static TexturePaint tWall = new TexturePaint((BufferedImage) iWall, rWall);
	
	
	public IsoTile(Orientation orientation, float startHeight, float endHeight, 
			int x, int y, String ref, ImageType type ) {
		this.fieldLocation = new Location(x, y);
		this.orientation   = orientation;

		this.startHeight = startHeight;
		this.endHeight   = endHeight;
		
		this.height = (startHeight + endHeight) / 2;
		this.type   = type;
		this.state  = TileState.NONE;
		this.name   = ref;
		
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
		
		
		tileImage = ResourceManager.instance().getTile(ref,horizontal,vertical);
	}
	
	public void invaildate(){
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		tileImage = ResourceManager.instance().getTile(name,horizontal,vertical);
	}
	
	public boolean contains(Point p) {
		return top != null && top.contains(p);
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

	public Rectangle getBounds(){
		return top.getBounds();
	}

	public AnimatedUnit removeUnit(){
		AnimatedUnit temp = unit;
		unit = null;
		return temp;
	}

	public void draw(int x, int y, Graphics g, boolean drawLeftSide, boolean drawRightSide) {
		assert orientation != null: orientation + " is null";
		switch (orientation) {
			case UP_TO_NORTH:
			case UP_TO_SOUTH:
				drawNorthSouth(x, y, g, false);
				break;
			case UP_TO_EAST:
			case UP_TO_WEST:
				drawEastWest(x, y, g, false, false);
				break;
			case EMPTY:
				break;
			default:
				assert false : orientation + "Not defined" ;
		}
	}

	Color lineColor = Color.BLACK;
	public void drawEastWest(int x, int y, Graphics _g, boolean topPloy, boolean topOnly) {
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
		
		if (topPloy && !topOnly) return;
		
		if (type == TEXTURED || type != TEXTURED && startHeight != endHeight) {
			g.setPaint(tGrass);
			g.fillPolygon(top);
			g.setPaint(old);
		} else {
			int f = Math.round(x - horizontal / 2f);
			g.drawImage(tileImage,  f, (int) (y - h2), null);
		}

		
		if (state != TileState.NONE) {
			Composite oldC = g.getComposite();
			AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			g.setComposite(alphaComposite);
			g.setColor(state.colour);
			g.fillPolygon(top);
			g.setComposite(oldC);
		}
		
		if (isSelected() || topOnly) {
			Composite oldC = g.getComposite();
			AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
			g.setComposite(alphaComposite);
			g.setColor(TileState.SELECTED.colour);
			g.fillPolygon(top);
			g.setComposite(oldC);
		}
		
		if (topPloy) return;
		
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

			poly = new Polygon(new int[] {
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

//
//		g.setColor(lineColor); // Outline the top of the tile
//		g.drawPolygon(new Polygon(new int[] {
//				x,
//				x_hor_div_2,
//				x,
//				neg_x_hor_div_2 },
//				new int[] {
//						(int) (y - h1),
//						neg_y_h2_vet_div_2,
//						neg_y_h2_vet,
//						neg_y_h1_vet_div_2 }
//				, 4));
//		
		g.setColor(oldColor);
	}

	/**
	 * Draw a tile that slants to the north or the south. The methods for drawing are very similar,
	 * so they can be merged into one method.
	 * 
	 * @category unused
	 */
	public void drawNorthSouth(int x, int y, Graphics g, boolean toponly) {

		int finalHeight = (int) (MapSettings.tileHeight * MapSettings.zoom);
		int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		int HEIGHT1 = orientation == Orientation.UP_TO_NORTH ? (int) (finalHeight * startHeight)
				: (int) (finalHeight * endHeight);
		int HEIGHT2 = orientation == UP_TO_NORTH ? (int) (finalHeight * endHeight)
				: (int) (finalHeight * startHeight);

		Color oldColor = g.getColor();
		g.setColor(state.colour);
		top = new Polygon(new int[] {
				x,
				x + horizontal / 2,
				x, x - horizontal / 2 },
				new int[] {
						y - HEIGHT2,
						y - HEIGHT2 + vertical / 2,
						y - HEIGHT1 + vertical,
						y - HEIGHT1 + vertical / 2 }
				, 4);
		if (toponly) return;
		
		g.fillPolygon(top);
		
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
	public int getX(){
		return fieldLocation.getX();
	}
	
	/** @category Generated */
	public int getY(){
		return fieldLocation.getY();
	}
	
	
	/** @category Generated */
	public Location getLocation() {
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

	/** @category Generated Getter */
	public boolean isSelected() {
		return selected;
	}

	/** @category Generated Setter */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		String s = String.format("IsoTile [%s, way=%s, state=%s height=%s,selected=%s, unit=%s]",
				fieldLocation,orientation,state, height, selected, unit);
//		s += "\n" +  Arrays.toString(top.xpoints);
//		s += "\n" +  Arrays.toString(top.ypoints);
//		s += "\n" + top.getBounds();
		return s;
	}
}