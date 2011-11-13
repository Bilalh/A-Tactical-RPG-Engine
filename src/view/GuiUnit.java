package view;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import common.gui.Sprite;
import common.gui.SpriteManager;

/**
 * @author bilalh
 */
public class GuiUnit {
	protected int gridX;
	protected int gridY;
	protected Sprite sprite;
	private UUID uuid; 
	
	public GuiUnit(int gridX,int gridY,String ref, UUID uuid) {
		this.sprite = SpriteManager.instance().getSprite(ref);
		this.gridX = gridX;
		this.gridY = gridY;
		this.uuid = uuid;
	} 
	
	public void draw(Graphics g, final MapTile[][] tiles, int x, int y) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.5);
		sprite.draw(g,xPos,yPos);
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
	
	public Collection<Point> getVaildTiles(){
		int sizeX = 5, sizeY = 5;

		Point pos = new Point(gridX, gridY);
		Collection<Point> points = new HashSet<Point>();		

		points.add(pos);
		final int[][] dirs = {
				{0,1},  // up 
				{0,-1},  // down
				{-1,0}, // left
				{1,0},   // right
		};		

		final int[][] dirs2 = {
				{1,1},   
				{1,-1}, 
				{-1,1}, 
				{-1,-1},
		};	
		
		for (int i = 1; i <= 2; i++) {
			for (int[] is : dirs) {
				final int nx = pos.x +is[0] *i;
				final int ny = pos.y +is[1] *i;
				if (nx  >= 0 &&  nx < sizeX && ny >= 0 &&  ny < sizeY ){
					points.add(new Point(nx,ny));
				}
			}
			for (int[] is : dirs2) {
				final int nx = pos.x +is[0] *(i-1);
				final int ny = pos.y +is[1] *(i-1);
				if (nx  >= 0 &&  nx < sizeX && ny >= 0 &&  ny < sizeY ){
					points.add(new Point(nx,ny));
				}
			}
			
			System.out.println();
		}

		return points;
	}
	
}
