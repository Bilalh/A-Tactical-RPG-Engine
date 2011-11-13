package engine;

import java.awt.Point;
import java.util.*;

import javax.vecmath.Point2d;

import engine.interfaces.IModelUnit;

/**
 * @author bilalh
 */
public class Unit implements IModelUnit {

	private String name;

	private int maxHp;
	private int currentHp;

	private int move;
	private int strength;

	private int gridX;
	private int gridY;

	final private UUID uuid;

	/** @category Generated */
	public Unit(String name, int maxHp, int move, int strength) {
		this.name = name;
		this.maxHp = maxHp;
		this.currentHp = maxHp;
		this.move = move;
		this.strength = strength;
		uuid = UUID.randomUUID();
	}

	/** @category Generated */
	@Override
	public int getMaxHp() {
		return maxHp;
	}
	/** @category Generated */
	@Override
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}
	/** @category Generated */
	@Override
	public int getCurrentHp() {
		return currentHp;
	}
	/** @category Generated */
	@Override
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}
	/** @category Generated */
	@Override
	public int getMove() {
		return move;
	}
	/** @category Generated */
	@Override
	public void setMove(int move) {
		this.move = move;
	}
	/** @category Generated */
	@Override
	public int getStrength() {
		return strength;
	}
	/** @category Generated */
	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}
	/** @category Generated */
	@Override
	public String getName() {
		return name;
	}

	/** @category Generated */
	@Override
	public int getGridX() {
		return gridX;
	}

	/** @category Generated */
	@Override
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated */
	@Override
	public int getGridY() {
		return gridY;
	}

	/** @category Generated */
	@Override
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}

	public void setLocation(Point p){
		gridX = p.x;
		gridY = p.y;
	}
	
	@Override
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
		
		for (int i = 1; i <= move; i++) {
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

	public static void main(String[] args){
		IModelUnit u = new Unit("d", 23, 2, 3);
		u.setGridX(0);
		u.setGridY(0);
		Collection<Point> ps =  u.getVaildTiles();
		System.out.println(ps);
	}

}
