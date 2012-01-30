package view;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.UUID;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.GuiTile;

import common.Location;
import common.gui.Sprite;
import common.gui.SpriteManager;
import common.interfaces.ILocation;
import common.interfaces.IMapUnit;

/**
 * @author bilalh
 */
public class GuiUnit {
	private static final Logger log = Logger.getLogger(GuiUnit.class);
	protected int gridX;
	protected int gridY;
	protected Sprite sprite;
	protected IMapUnit unit;
	protected Rectangle2D bounds;
	
	public GuiUnit(int gridX,int gridY,String ref) {
		this.sprite = SpriteManager.instance().getSprite(ref);
		this.gridX = gridX;
		this.gridY = gridY;
	} 
	
	public void draw(Graphics g, final GuiTile[][] tiles, int x, int y) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.25);
		sprite.draw(g,xPos,yPos);
		bounds  = new Rectangle2D.Float(xPos,yPos+getHeight(),getWidth(),getHeight());
	}

	public boolean isIntersecting(GuiTile t, int x, int y){
		return bounds.contains(x,y);
	}
	
	public Point topLeftPoint(GuiTile[][] tiles, int x, int y){
		final Point result =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		result.translate(-sprite.getWidth()/2, (int) (-sprite.getHeight()/1.5));
		return result;
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
	
	public Location getLocation(){
		return new Location(gridX,gridY);
	}

	public void setLocation(ILocation l){
		gridX = l.getX();
		gridY = l.getY();
	}
	
	/** @category Generated Getter */
	public int getWidth() {
		return sprite.getWidth();
	}

	/** @category Generated Getter */
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
	
}
