package view.units;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.IsoTile;

import common.Location;
import common.enums.Direction;
import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import common.spritesheet.SpriteSheet;

import config.Config;
import engine.unit.UnitImages;

/**
 * @author bilalh
 */
public class GuiUnit {
	private static final Logger log = Logger.getLogger(GuiUnit.class);
	
	protected IMapUnit unit;
	protected int gridX;
	protected int gridY;
	
	protected SpriteSheet spriteSheet;
	protected UnitImages images;
	
	protected BufferedImage sprite;
	protected Direction direction;
	
	protected Rectangle2D bounds;
	
	public GuiUnit(int gridX,int gridY, IUnit u) {
		this.gridX = gridX;
		this.gridY = gridY;
		assert u != null;
		images = u.getImageData(); 
		assert images != null;
		Logf.info(log,"Using sheet %s", images.getSpriteSheetLocation());
		spriteSheet = Config.loadSpriteSheet(images.getSpriteSheetLocation());
		setDirection(Direction.EAST);
		assert direction != null : "Direction Should not be null";
	} 
	
	public void draw(Graphics g, final IsoTile[][] tiles, int drawX, int drawY) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(drawX,drawY);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.3);
		g.drawImage(sprite,xPos,yPos,null);
		bounds  = new Rectangle2D.Float(xPos,yPos+getHeight(),getWidth(),getHeight());
	}

	public boolean isIntersecting(IsoTile t, int x, int y){
		assert t !=null;
		return bounds.contains(x,y);
	}
	
	public Point topLeftPoint(IsoTile[][] tiles, int x, int y){
		final Point result =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		result.translate(-sprite.getWidth()/2, (int) (-sprite.getHeight()/1.5));
		return result;
	}
	
	public Location getLocation(){
		return new Location(gridX,gridY);
	}

	public void setLocation(ILocation l){
		assert l != null;
		gridX = l.getX();
		gridY = l.getY();
	}

	/** @category Generated */
	public int getGridX() {
		return gridX;
	}

	/** @category Generated */
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated */
	public int getGridY() {
		return gridY;
	}

	/** @category Generated */
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
	
	/** @category Generated */
	public int getWidth() {
		return sprite.getWidth();
	}

	/** @category Generated */
	public int getHeight() {
		return sprite.getHeight();
	}

	/** @category Generated */
	public IMapUnit getUnit() {
		return unit;
	}

	/** @category Generated */
	public void setMapUnit(IMapUnit unit) {
		this.unit = unit;
	}

	/** @category Generated */
	public Direction getDirection() {
		return direction;
	}

	/** @category Generated */
	public void setDirection(Direction direction) {
		this.direction = direction;
		sprite = spriteSheet.getSpriteImage(direction.reference()+"0");
	}
	
}
