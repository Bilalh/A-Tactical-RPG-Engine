package engine;

import java.awt.Point;
import java.util.*;

import view.notifications.ChooseUnitsNotifications;
import view.notifications.PlayersTurnNotification;
import view.notifications.UserMovedNotification;

import common.interfaces.INotification;
import common.interfaces.IUnit;
import engine.interfaces.IModelUnit;



/**
 * @author bilalh
 */
public class Map extends Observable {

	private Tile[][] field;
	private int width;
	private int height;
	
	private AIPlayer ai;
	private Player player;
	private ArrayList<Unit> selectedUnits;
	
	boolean playersTurn; // false aiplayer

	/** @category Constructor */
	public Map(String name, Player player) {
		this.player = player;
		loadSettings(name);
		setUpAI();
		playersTurn = true;
	}

	private void setUpAI() {
		AIPlayer ai = new AIPlayer();
		Unit u = new Unit("ai-1", 20, 4, 4);
		u.setGridX(width-1);
		u.setGridY(0);
		ai.addUnit(u);
		
		u = new Unit("ai-2", 10, 2, 10);
		u.setGridX(width-1);
		u.setGridY(1);
		ai.addUnit(u);
		this.ai = ai;
	}

	public void setUsersUnits(ArrayList<Unit> units){
		selectedUnits= units;
		INotification n =  new PlayersTurnNotification();
		setChanged();
		notifyObservers(n);
	}

	private void loadSettings(String name) {
		width = 10; 
		height = 10;
		field = new Tile[width][height];
//		Random r = new Random();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
//				int first = r.nextInt(3)+1;
//				field[i][j] = new Tile(first, first + r.nextInt(2));
				field[i][j] = new Tile(1,1);
			}
		}
	}


	public void start() {
		INotification n =  new ChooseUnitsNotifications(player.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	public void moveUnit(IModelUnit u, Point p){
		u.setLocation(p);
		setChanged();
		INotification n =  new UserMovedNotification(u);
		setChanged();
		notifyObservers(n);
	}
	
	public ArrayList<Unit> getUnits(){
		return player.getUnits();
	}

	/** @category Generated */
	public Tile[][] getField() {
		return field;
	}
	
	/** @category Generated */
	public boolean isPlayersTurn() {
		return playersTurn;
	}

	HashSet<Point> points  = new HashSet<Point>();
	final int[][] dirs = {
			{0,1},  // up 
			{0,-1},  // down
			{-1,0}, // left
			{1,0},   // right
	};		
	
	private void checkAround(Point p, int movmentLeft){
		if ( movmentLeft - field[p.x][p.y].getCost()  < 1 ) return; 
		System.out.println(p);
		points.add(p);
		
		for (int[] is : dirs) { 
			Point pp = new Point(p);
			pp.translate(is[0], is[1]);
			if (pp.x  >= 0 &&  pp.x < width && pp.y >= 0 &&  pp.y < height ){
				checkAround(pp, movmentLeft-1);
			}
		}
		
	}
	
	public Collection<Point> getMovementRange(IModelUnit u) {
		points.clear();
		checkAround(u.getLocation(), u.getMove()+1);
		return points;
	}
	
}
