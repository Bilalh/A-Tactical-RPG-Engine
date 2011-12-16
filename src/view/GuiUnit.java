package view;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import common.gui.Sprite;
import common.gui.SpriteManager;
import common.interfaces.IUnit;

/**
 * @author bilalh
 */
public class GuiUnit implements IUnit {
	protected int gridX;
	protected int gridY;
	protected Sprite sprite;
	protected IUnit unit;
	
	public GuiUnit(int gridX,int gridY,String ref, IUnit unit) {
		this.sprite = SpriteManager.instance().getSprite(ref);
		this.gridX = gridX;
		this.gridY = gridY;
		this.unit = unit;
	} 
	
	public void draw(Graphics g, final MapTile[][] tiles, int x, int y) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.5);
		sprite.draw(g,xPos,yPos);
	}

	/** @category Generated */
	@Override
	public int getGridX() {
		return gridX;
	}

	/** @category Generated */
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated */
	@Override
	public int getGridY() {
		return gridY;
	}

	/** @category Generated */
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
	
	int movement = 2;
	int sizeX = 10, sizeY = 10;

	final int[][] dirs = {
			{0,1},  // up 
			{0,-1},  // down
			{-1,0}, // left
			{1,0},   // right
	};		

	HashSet<Point> points  = new HashSet<Point>();
	private void checkAround(Point p, int movmentLeft){
		if ( movmentLeft < 1 ) return; 
		points.add(p);
		
		for (int[] is : dirs) { 
			if (p.x  >= 0 &&  p.x < sizeX && p.y >= 0 &&  p.y < sizeY ){
				Point pp = new Point(p);
				pp.translate(is[0], is[1]);
				checkAround(pp, movmentLeft-1);
			}
		}
		
	}

	@Override
	public int getMaxHp() {
		return unit.getMaxHp();
	}

	@Override
	public int getCurrentHp() {
		return unit.getCurrentHp();
	}

	@Override
	public int getMove() {
		return unit.getMove();
	}

	@Override
	public int getStrength() {
		return unit.getStrength();
	}

	@Override
	public String getName() {
		return unit.getName();
	}

	@Override
	public UUID getUuid() {
		return unit.getUuid();
	}
	
}
