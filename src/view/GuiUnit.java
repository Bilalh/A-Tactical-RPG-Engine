package view;

import java.awt.Graphics;
import java.awt.Point;
import java.util.UUID;

import common.Location;
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

	public Point topLeftPoint(MapTile[][] tiles, int x, int y){
		final Point result =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		result.translate(-sprite.getWidth()/2, (int) (-sprite.getHeight()/1.5));
		return result;
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
	
	public Location getPostion(){
		return new Location(gridX,gridY);
	}

	/** @category Generated Getter */
	public int getWidth() {
		return sprite.getWidth();
	}

	/** @category Generated Getter */
	public int getHeight() {
		return sprite.getHeight();
	}
	
}
