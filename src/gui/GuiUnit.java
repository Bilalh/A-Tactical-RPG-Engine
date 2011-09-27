package gui;

import java.awt.Graphics;
import java.awt.Point;

import common.gui.Sprite;
import common.gui.SpriteManager;

/**
 * @author bilalh
 */
public class GuiUnit {
	protected int gridX;
	protected int gridY;
	protected Sprite sprite;
	
	public GuiUnit(String ref,int gridX,int gridY) {
		this.sprite = SpriteManager.instance().getSprite(ref);
		this.gridX = gridX;
		this.gridY = gridY;
	} 
	
	public void draw(Graphics g, final MapTile[][] tiles, int x, int y) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.5);
		sprite.draw(g,xPos,yPos);
	}
	
	
	
}
